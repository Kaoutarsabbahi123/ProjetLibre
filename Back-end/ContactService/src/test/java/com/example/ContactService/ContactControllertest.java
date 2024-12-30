package com.example.ContactService;


import com.example.ContactService.Controller.ContactController;
import com.example.ContactService.Entity.Contact;
import com.example.ContactService.Service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private Contact contact;

    @BeforeEach
    void setUp() {
        contact = new Contact(1L, 101L, "0123456789", "0123456789", "contact@example.com");
    }

    @Test
    void testCreateContact() throws Exception {
        when(contactService.saveContact(any(Contact.class))).thenReturn(contact);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contact.getId()))
                .andExpect(jsonPath("$.fkIdLaboratoire").value(contact.getFkIdLaboratoire()))
                .andExpect(jsonPath("$.email").value(contact.getEmail()));
    }

    @Test
    void testGetContactsByLaboratoireId() throws Exception {
        List<Contact> contacts = Arrays.asList(
                contact,
                new Contact(2L, 101L, "0987654321", "0987654321", "contact2@example.com")
        );

        when(contactService.getContactsByLaboratoireId(101L)).thenReturn(contacts);

        mockMvc.perform(get("/api/contacts/laboratoire/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].email").value("contact@example.com"))
                .andExpect(jsonPath("$[1].email").value("contact2@example.com"));
    }

    @Test
    void testGetContactById_Found() throws Exception {
        when(contactService.getContactById(1L)).thenReturn(contact);

        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contact.getId()))
                .andExpect(jsonPath("$.email").value(contact.getEmail()));
    }

    @Test
    void testGetContactById_NotFound() throws Exception {
        when(contactService.getContactById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/contacts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateContact() throws Exception {
        Contact updatedContact = new Contact(1L, 101L, "1112223333", "4445556666", "updated@example.com");

        when(contactService.getContactById(1L)).thenReturn(contact);
        when(contactService.saveContact(any(Contact.class))).thenReturn(updatedContact);

        mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedContact.getId()))
                .andExpect(jsonPath("$.numTel").value(updatedContact.getNumTel()))
                .andExpect(jsonPath("$.email").value(updatedContact.getEmail()));
    }

    @Test
    void testUpdateContact_NotFound() throws Exception {
        Contact updatedContact = new Contact(99L, 101L, "1112223333", "4445556666", "updated@example.com");

        when(contactService.getContactById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/contacts/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedContact)))
                .andExpect(status().isNotFound());
    }
}

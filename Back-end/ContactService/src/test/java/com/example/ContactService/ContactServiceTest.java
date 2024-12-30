package com.example.ContactService;


import com.example.ContactService.Entity.Contact;
import com.example.ContactService.Repository.ContactRepository;
import com.example.ContactService.Service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {

    private ContactRepository contactRepository;
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactRepository = mock(ContactRepository.class); // Mock du repository
        contactService = new ContactService(contactRepository); // Service avec mock
    }

    @Test
    void testSaveContact() {
        // Données de test
        Contact contact = new Contact(1L, 101L, "0123456789", "0123456789", "contact@example.com");

        // Comportement attendu du mock
        when(contactRepository.save(contact)).thenReturn(contact);

        // Appel de la méthode
        Contact savedContact = contactService.saveContact(contact);

        // Assertions
        assertNotNull(savedContact);
        assertEquals("0123456789", savedContact.getNumTel());
        assertEquals("contact@example.com", savedContact.getEmail());
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    void testGetContactsByLaboratoireId() {
        // Données de test
        Long laboratoireId = 101L;
        List<Contact> contacts = Arrays.asList(
                new Contact(1L, laboratoireId, "0123456789", "0123456789", "contact1@example.com"),
                new Contact(2L, laboratoireId, "0987654321", "0987654321", "contact2@example.com")
        );

        // Comportement attendu du mock
        when(contactRepository.findByFkIdLaboratoire(laboratoireId)).thenReturn(contacts);

        // Appel de la méthode
        List<Contact> result = contactService.getContactsByLaboratoireId(laboratoireId);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("contact1@example.com", result.get(0).getEmail());
        verify(contactRepository, times(1)).findByFkIdLaboratoire(laboratoireId);
    }

    @Test
    void testGetContactById() {
        // Données de test
        Long contactId = 1L;
        Contact contact = new Contact(contactId, 101L, "0123456789", "0123456789", "contact@example.com");

        // Comportement attendu du mock
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        // Appel de la méthode
        Contact result = contactService.getContactById(contactId);

        // Assertions
        assertNotNull(result);
        assertEquals("0123456789", result.getNumTel());
        assertEquals("contact@example.com", result.getEmail());
        verify(contactRepository, times(1)).findById(contactId);
    }

    @Test
    void testGetContactById_NotFound() {
        // Données de test
        Long contactId = 2L;

        // Comportement attendu du mock
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // Appel de la méthode
        Contact result = contactService.getContactById(contactId);

        // Assertions
        assertNull(result);
        verify(contactRepository, times(1)).findById(contactId);
    }
}

package com.example.AddressService;


import com.example.AddressService.Entities.Address;
import com.example.AddressService.Services.AddressService;
import com.example.AddressService.Controllers.AddressController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AddressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateAddress() throws Exception {
        Address address = Address.builder()
                .id(1)
                .numVoie("123")
                .nomVoie("Main St")
                .codePostal("75001")
                .ville("Paris")
                .commune("Paris Commune")
                .fkIdcontact(101L)
                .build();

        when(addressService.createAddress(any(Address.class))).thenReturn(address);

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numVoie").value("123"))
                .andExpect(jsonPath("$.ville").value("Paris"));

        verify(addressService, times(1)).createAddress(any(Address.class));
    }

    @Test
    void testGetAddressesByContactId() throws Exception {
        Long contactId = 101L;
        List<Address> addresses = new ArrayList<>();
        addresses.add(Address.builder()
                .id(1)
                .numVoie("123")
                .nomVoie("Main St")
                .codePostal("75001")
                .ville("Paris")
                .commune("Paris Commune")
                .fkIdcontact(contactId)
                .build());

        when(addressService.findByFkIdcontact(contactId)).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses/contact/{contactId}", contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numVoie").value("123"))
                .andExpect(jsonPath("$[0].ville").value("Paris"));

        verify(addressService, times(1)).findByFkIdcontact(contactId);
    }

    @Test
    void testUpdateAddress() throws Exception {
        Integer id = 1;
        Address address = Address.builder()
                .id(id)
                .numVoie("123")
                .nomVoie("Main St Updated")
                .codePostal("75002")
                .ville("Lyon")
                .commune("Lyon Commune")
                .fkIdcontact(101L)
                .build();

        when(addressService.findById(id)).thenReturn(Optional.of(address));
        when(addressService.updateAddress(any(Address.class))).thenReturn(address);

        mockMvc.perform(put("/api/addresses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomVoie").value("Main St Updated"))
                .andExpect(jsonPath("$.ville").value("Lyon"));

        verify(addressService, times(1)).findById(id);
        verify(addressService, times(1)).updateAddress(any(Address.class));
    }

    @Test
    void testGetAddressById() throws Exception {
        Integer id = 1;
        Address address = Address.builder()
                .id(id)
                .numVoie("123")
                .nomVoie("Main St")
                .codePostal("75001")
                .ville("Paris")
                .commune("Paris Commune")
                .fkIdcontact(101L)
                .build();

        when(addressService.findById(id)).thenReturn(Optional.of(address));

        mockMvc.perform(get("/api/addresses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numVoie").value("123"))
                .andExpect(jsonPath("$.ville").value("Paris"));

        verify(addressService, times(1)).findById(id);
    }

    @Test
    void testGetAddressById_NotFound() throws Exception {
        Integer id = 1;

        when(addressService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/addresses/{id}", id))
                .andExpect(status().isNotFound());

        verify(addressService, times(1)).findById(id);
    }
}

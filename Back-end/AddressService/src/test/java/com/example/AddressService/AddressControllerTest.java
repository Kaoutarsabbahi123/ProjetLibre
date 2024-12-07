package com.example.AddressService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.AddressService.Controllers.AddressController;
import com.example.AddressService.Entities.Address;
import com.example.AddressService.Services.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

public class AddressControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateAddress() throws Exception {
        Address address = new Address();
        address.setVille("Paris");

        when(addressService.createAddress(any())).thenReturn(address);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAddressById() throws Exception {
        Address address = new Address();
        address.setVille("Paris");

        when(addressService.getAddressById(1)).thenReturn(Optional.of(address));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ville").value("Paris"));
    }
}

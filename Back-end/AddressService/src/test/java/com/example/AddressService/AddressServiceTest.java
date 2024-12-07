package com.example.AddressService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.AddressService.Entities.Address;
import com.example.AddressService.Repository.AddressRepository;
import com.example.AddressService.Services.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        address = Address.builder()
                .numVoie("12B")
                .nomVoie("Rue de la Paix")
                .codePostal("75002")
                .ville("Paris")
                .commune("Montmartre")
                .country("France")
                .build();
    }

    // Test de la création d'une adresse
    @Test
    public void testCreateAddress() {
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        Address savedAddress = addressService.createAddress(address);
        assertNotNull(savedAddress);
        assertEquals("Paris", savedAddress.getVille());
        verify(addressRepository, times(1)).save(address);
    }

    // Test de la récupération des adresses avec pagination
    @Test
    public void testGetAllAddresses() {
        List<Address> addressList = new ArrayList<>();
        addressList.add(address);
        Page<Address> addressPage = new PageImpl<>(addressList);

        when(addressRepository.findAll(any(PageRequest.class))).thenReturn(addressPage);
        Page<Address> result = addressService.getAllAddresses(0, 10);

        assertEquals(1, result.getTotalElements());
        verify(addressRepository, times(1)).findAll(any(PageRequest.class));
    }

    // Test de la récupération d'une adresse par ID
    @Test
    public void testGetAddressById() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        Optional<Address> foundAddress = addressService.getAddressById(1);

        assertTrue(foundAddress.isPresent());
        assertEquals("Paris", foundAddress.get().getVille());
    }

    // Test de la mise à jour d'une adresse
    @Test
    public void testUpdateAddress() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        address.setVille("Lyon");
        Address updatedAddress = addressService.updateAddress(1, address);

        assertEquals("Lyon", updatedAddress.getVille());
    }

    // Test de la suppression d'une adresse
    @Test
    public void testDeleteAddress() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).deleteById(anyInt());

        addressService.deleteAddress(1);
        verify(addressRepository, times(1)).deleteById(1);
    }
}
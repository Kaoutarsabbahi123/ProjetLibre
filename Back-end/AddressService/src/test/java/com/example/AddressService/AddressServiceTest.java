package com.example.AddressService;

import com.example.AddressService.Entities.Address;
import com.example.AddressService.Services.AddressService;
import com.example.AddressService.Repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAddress() {
        Address address = Address.builder()
                .id(1)
                .numVoie("123")
                .nomVoie("Main St")
                .codePostal("75001")
                .ville("Paris")
                .commune("Paris Commune")
                .fkIdcontact(101L)
                .build();

        when(addressRepository.save(address)).thenReturn(address);

        Address createdAddress = addressService.createAddress(address);

        assertNotNull(createdAddress);
        assertEquals(1, createdAddress.getId());
        assertEquals("123", createdAddress.getNumVoie());
        assertEquals("Paris", createdAddress.getVille());
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void testFindByFkIdcontact() {
        Long contactId = 101L;
        List<Address> addresses = new ArrayList<>();
        Address address = Address.builder()
                .id(1)
                .numVoie("123")
                .nomVoie("Main St")
                .codePostal("75001")
                .ville("Paris")
                .commune("Paris Commune")
                .fkIdcontact(contactId)
                .build();
        addresses.add(address);

        when(addressRepository.findByFkIdcontact(contactId)).thenReturn(addresses);

        List<Address> result = addressService.findByFkIdcontact(contactId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getNumVoie());
        assertEquals("Paris", result.get(0).getVille());
        verify(addressRepository, times(1)).findByFkIdcontact(contactId);
    }

    @Test
    void testGetAddressById() {
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

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        Optional<Address> result = addressService.getAddressById(id);

        assertTrue(result.isPresent());
        assertEquals("Paris", result.get().getVille());
        verify(addressRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateAddress() {
        Address address = Address.builder()
                .id(1)
                .numVoie("123")
                .nomVoie("Main St Updated")
                .codePostal("75002")
                .ville("Lyon")
                .commune("Lyon Commune")
                .fkIdcontact(101L)
                .build();

        when(addressRepository.save(address)).thenReturn(address);

        Address updatedAddress = addressService.updateAddress(address);

        assertNotNull(updatedAddress);
        assertEquals("Main St Updated", updatedAddress.getNomVoie());
        assertEquals("Lyon", updatedAddress.getVille());
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void testFindById() {
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

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        Optional<Address> result = addressService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("Paris", result.get().getVille());
        verify(addressRepository, times(1)).findById(id);
    }
}

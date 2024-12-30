package com.example.AddressService.Services;

import com.example.AddressService.Entities.Address;
import com.example.AddressService.Repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    // Ajouter une nouvelle adresse
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    public List<Address> findByFkIdcontact(Long contactId) {
        return addressRepository.findByFkIdcontact(contactId);
    }

    // Récupérer une adresse par ID
    public Optional<Address> getAddressById(Integer id) {
        return addressRepository.findById(id);
    }

    public Address updateAddress(Address address) {
        // Effectuer la mise à jour de l'adresse dans la base de données
        return addressRepository.save(address); // Sauvegarde l'adresse mise à jour
    }
    public Optional<Address> findById(Integer id) {
        return addressRepository.findById(id); // Utilise la méthode de Spring Data JPA
    }
}

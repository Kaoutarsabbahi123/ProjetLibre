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

    // Récupérer toutes les adresses avec pagination
    public Page<Address> getAllAddresses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return addressRepository.findAll(pageable);
    }

    // Récupérer une adresse par ID
    public Optional<Address> getAddressById(Integer id) {
        return addressRepository.findById(id);
    }

    // Mettre à jour une adresse
    public Address updateAddress(Integer id, Address newAddress) {
        return addressRepository.findById(id).map(address -> {
            address.setNumVoie(newAddress.getNumVoie());
            address.setNomVoie(newAddress.getNomVoie());
            address.setCodePostal(newAddress.getCodePostal());
            address.setVille(newAddress.getVille());
            address.setCommune(newAddress.getCommune());
            address.setCountry(newAddress.getCountry());
            return addressRepository.save(address);
        }).orElseThrow(() -> new RuntimeException("Adresse non trouvée avec l'ID : " + id));
    }

    // Supprimer une adresse par ID
    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }
}

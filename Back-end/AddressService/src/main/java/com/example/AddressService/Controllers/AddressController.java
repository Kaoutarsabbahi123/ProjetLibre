package com.example.AddressService.Controllers;

import com.example.AddressService.Entities.Address;
import com.example.AddressService.Services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // Ajouter une nouvelle adresse
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address savedAddress = addressService.createAddress(address);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/contact/{contactId}")
    public List<Address> getAddressesByLaboratoireId(@PathVariable Long contactId) {
        return addressService.findByFkIdcontact(contactId);
    }
    // Mettre à jour une adresse par ID
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Integer id, @RequestBody Address address) {
        try {
            // Vérification de l'existence de l'adresse avant la mise à jour
            Optional<Address> existingAddress = addressService.findById(id);
            if (!existingAddress.isPresent()) {
                return ResponseEntity.notFound().build(); // Retourner Not Found si l'adresse n'existe pas
            }

            // Mise à jour de l'adresse
            address.setId(id); // S'assurer que l'adresse a le bon ID pour la mise à jour
            Address updatedAddress = addressService.updateAddress(address);

            return ResponseEntity.ok(updatedAddress); // Retourner l'adresse mise à jour
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Integer id) {
        Optional<Address> address = addressService.findById(id); // Recherche de l'adresse par ID
        if (address.isPresent()) {
            return ResponseEntity.ok(address.get()); // Retourner l'adresse si elle existe
        } else {
            return ResponseEntity.notFound().build(); // Retourner Not Found si l'adresse n'existe pas
        }
    }


    // Supprimer une adresse par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
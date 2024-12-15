package com.example.ContactService.Controller;

import com.example.ContactService.Entity.Contact;
import com.example.ContactService.Service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Ajouter un contact
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.ok(contactService.saveContact(contact));
    }

    // Récupérer les contacts par ID du laboratoire
    @GetMapping("/laboratoire/{laboratoireId}")
    public ResponseEntity<List<Contact>> getContactsByLaboratoireId(@PathVariable Long laboratoireId) {
        return ResponseEntity.ok(contactService.getContactsByLaboratoireId(laboratoireId));
    }

    // Récupérer un contact par ID
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contact);
    }
    // Mettre à jour un contact par ID
    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        try {
            // Vérifier si le contact existe
            Contact existingContact = contactService.getContactById(id);
            if (existingContact == null) {
                return ResponseEntity.notFound().build(); // Retourner Not Found si le contact n'existe pas
            }

            // Mettre à jour le contact avec les nouvelles informations
            contact.setId(id); // S'assurer que l'ID est le bon
            Contact updatedContact = contactService.saveContact(contact);

            return ResponseEntity.ok(updatedContact); // Retourner le contact mis à jour
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Supprimer un contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}

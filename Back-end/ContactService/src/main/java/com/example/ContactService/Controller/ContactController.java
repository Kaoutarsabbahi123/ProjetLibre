package com.example.ContactService.Controller;

import com.example.ContactService.Entity.Contact;
import com.example.ContactService.Service.ContactService;
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

    // Supprimer un contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}

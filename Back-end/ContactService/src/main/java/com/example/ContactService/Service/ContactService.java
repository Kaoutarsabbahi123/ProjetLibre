package com.example.ContactService.Service;


import com.example.ContactService.Entity.Contact;
import com.example.ContactService.Repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // Ajouter un contact
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    // Récupérer tous les contacts d'un laboratoire
    public List<Contact> getContactsByLaboratoireId(Long laboratoireId) {
        return contactRepository.findByFkIdLaboratoire(laboratoireId);
    }

    // Récupérer un contact par ID
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }

    // Supprimer un contact
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}

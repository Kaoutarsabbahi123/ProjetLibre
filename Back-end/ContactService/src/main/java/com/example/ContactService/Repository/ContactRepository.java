package com.example.ContactService.Repository;



import com.example.ContactService.Entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Si besoin, ajoute des méthodes spécifiques ici
    List<Contact> findByFkIdLaboratoire(Long fkIdLaboratoire);
}

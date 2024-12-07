package com.example.LaboratoryService.Repository;


import com.example.LaboratoryService.Entity.Laboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {
    // Si besoin, ajoute des méthodes spécifiques ici
}

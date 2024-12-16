package com.example.LaboratoryService.Repository;


import com.example.LaboratoryService.Entity.Laboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {
    // Si besoin, ajoute des méthodes spécifiques ici
    Optional<Laboratoire> findById(Long id);
    @Query("SELECT l FROM Laboratoire l " +
            "WHERE (LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(l.nrc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR STR(l.dateActivation) LIKE CONCAT('%', :keyword, '%') " +
            "OR STR(l.active) LIKE CONCAT('%', :keyword, '%'))")
    List<Laboratoire> searchLaboratoiresByKeyword(@Param("keyword") String keyword);

}

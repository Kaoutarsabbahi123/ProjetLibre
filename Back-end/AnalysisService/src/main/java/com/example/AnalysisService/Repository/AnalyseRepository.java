package com.example.AnalysisService.Repository;

import com.example.AnalysisService.Entity.Analyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AnalyseRepository extends JpaRepository<Analyse, Integer> {
    @Query("SELECT a FROM Analyse a WHERE a.fkIdLaboratoire = :idLaboratoire AND a.archive = false")
    List<Analyse> findNonArchivedByLaboratoireId(@Param("idLaboratoire") Long idLaboratoire);
}

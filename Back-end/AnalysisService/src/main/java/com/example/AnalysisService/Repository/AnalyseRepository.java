package com.example.AnalysisService.Repository;

import com.example.AnalysisService.Entity.Analyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyseRepository extends JpaRepository<Analyse, Integer> {

}

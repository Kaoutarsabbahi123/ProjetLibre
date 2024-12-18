package com.example.TestService.Repository;

import com.example.TestService.Entity.Epreuve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpreuveRepository extends JpaRepository<Epreuve, Integer> {
    List<Epreuve> findByFkIdAnalyse(Integer fkIdAnalyse);
}

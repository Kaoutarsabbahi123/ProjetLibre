package com.example.MicroTestService.Repository;


import com.example.MicroTestService.Entity.TestAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAnalyseRepository extends JpaRepository<TestAnalyse, Integer> {
    List<TestAnalyse> findByIdEpreuve(Integer idEpreuve);

}

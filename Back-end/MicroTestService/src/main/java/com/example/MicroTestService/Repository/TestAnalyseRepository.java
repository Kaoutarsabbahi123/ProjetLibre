package com.example.MicroTestService.Repository;


import com.example.MicroTestService.Entity.TestAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAnalyseRepository extends JpaRepository<TestAnalyse, Integer> {
}

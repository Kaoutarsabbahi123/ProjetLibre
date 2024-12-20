package com.example.MicroTestService.Service;

import com.example.MicroTestService.Entity.TestAnalyse;
import com.example.MicroTestService.Repository.TestAnalyseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestAnalyseService {
    private final TestAnalyseRepository testAnalyseRepository;
    public TestAnalyseService(TestAnalyseRepository testAnalyseRepository) {
        this.testAnalyseRepository= testAnalyseRepository;
    }
    public TestAnalyse saveTestAnalyse(TestAnalyse testAnalyse) {
        return testAnalyseRepository.save(testAnalyse);
    }
    public List<TestAnalyse> getTestsByIdEpreuve(Integer idEpreuve) {
        return testAnalyseRepository.findByIdEpreuve(idEpreuve);
    }
    public Optional<TestAnalyse> getTestsById(Integer id) {
        return testAnalyseRepository.findById(id);
    }
    public TestAnalyse updateTestAnalyse(TestAnalyse testAnalyse) {
        // Effectuer la mise à jour de l'adresse dans la base de données
        return testAnalyseRepository.save(testAnalyse); // Sauvegarde l'adresse mise à jour
    }
}

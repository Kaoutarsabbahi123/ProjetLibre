package com.example.MicroTestService.Service;

import com.example.MicroTestService.Entity.TestAnalyse;
import com.example.MicroTestService.Repository.TestAnalyseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

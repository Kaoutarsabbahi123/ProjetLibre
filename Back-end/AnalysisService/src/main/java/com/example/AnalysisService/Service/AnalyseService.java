package com.example.AnalysisService.Service;

import com.example.AnalysisService.Entity.Analyse;
import com.example.AnalysisService.Repository.AnalyseRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyseService {
    private final AnalyseRepository analyseRepository;
    public AnalyseService(AnalyseRepository analyseRepository) {
        this.analyseRepository= analyseRepository;
    }
    public Analyse saveAnalyse(Analyse analyse) {

        return analyseRepository.save(analyse);
    }
}

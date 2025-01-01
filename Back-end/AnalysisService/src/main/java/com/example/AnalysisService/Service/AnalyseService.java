package com.example.AnalysisService.Service;

import com.example.AnalysisService.Entity.Analyse;
import com.example.AnalysisService.Repository.AnalyseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyseService {
    private final AnalyseRepository analyseRepository;
    public AnalyseService(AnalyseRepository analyseRepository) {
        this.analyseRepository= analyseRepository;
    }
    public Analyse saveAnalyse(Analyse analyse) {

        return analyseRepository.save(analyse);
    }

    public List<Analyse> getAllAnalyses() {
        return analyseRepository.findAll();
    }
    public Optional<Analyse> getAnalyseById(Integer id){
        return analyseRepository.findById(id);
    }
    public List<Analyse> getNonArchivedAnalysesByLaboratoireId(Long laboratoireId) {
        return analyseRepository.findNonArchivedByLaboratoireId(laboratoireId);
    }
}

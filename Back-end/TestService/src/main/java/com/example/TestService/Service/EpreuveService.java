package com.example.TestService.Service;

import com.example.TestService.Entity.Epreuve;
import com.example.TestService.Repository.EpreuveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpreuveService {
    private final EpreuveRepository epreuveRepository;
    public EpreuveService(EpreuveRepository epreuveRepository) {
        this.epreuveRepository= epreuveRepository;
    }
    public Epreuve saveEpreuve(Epreuve epreuve) {

        return epreuveRepository.save(epreuve);
    }
    public List<Epreuve> getEpreuvesByFkIdAnalyse(Integer fkIdAnalyse) {
        return epreuveRepository.findByFkIdAnalyse(fkIdAnalyse);
    }
}

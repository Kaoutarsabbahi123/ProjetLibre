package com.example.TestService.Service;

import com.example.TestService.Entity.Epreuve;
import com.example.TestService.Repository.EpreuveRepository;
import org.springframework.stereotype.Service;

@Service
public class EpreuveService {
    private final EpreuveRepository epreuveRepository;
    public EpreuveService(EpreuveRepository epreuveRepository) {
        this.epreuveRepository= epreuveRepository;
    }
    public Epreuve saveEpreuve(Epreuve epreuve) {

        return epreuveRepository.save(epreuve);
    }
}

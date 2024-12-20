package com.example.TestService.Service;

import com.example.TestService.Entity.Epreuve;
import com.example.TestService.Repository.EpreuveRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<Epreuve> getEpreuveById(Integer id) {
        return epreuveRepository.findById(id);
    }
    public Epreuve updateEpreuve(Epreuve epreuve) {
        // Effectuer la mise à jour de l'adresse dans la base de données
        return epreuveRepository.save(epreuve); // Sauvegarde l'adresse mise à jour
    }
}

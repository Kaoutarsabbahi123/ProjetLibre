package com.example.LaboratoryService.Service;

import com.example.LaboratoryService.Entity.Laboratoire;
import com.example.LaboratoryService.Repository.LaboratoireRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaboratoireService {
    private final LaboratoireRepository laboratoireRepository;

    public LaboratoireService(LaboratoireRepository laboratoireRepository) {
        this.laboratoireRepository = laboratoireRepository;
    }

    // Ajouter un laboratoire
    public Laboratoire saveLaboratoire(Laboratoire laboratoire) {

        return laboratoireRepository.save(laboratoire);
    }
    public Optional<Laboratoire> getLaboratoireById(Long id) {
        return laboratoireRepository.findById(id);
    }

    // Récupérer tous les laboratoires
    public List<Laboratoire> getAllLaboratoires() {
        return laboratoireRepository.findAll();
    }


    // Supprimer un laboratoire
    public void deleteLaboratoire(Long id) {
        laboratoireRepository.deleteById(id);
    }
}
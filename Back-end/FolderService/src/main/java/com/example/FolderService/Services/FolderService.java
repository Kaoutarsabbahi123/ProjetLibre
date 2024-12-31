package com.example.FolderService.Services;

import com.example.FolderService.Entities.Folder;
import com.example.FolderService.Repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    public List<Folder> getAllDossiers() {
        return folderRepository.findAll();
    }
    public Folder createFolder(Folder folder) {
        folder.setActive(true);
        return folderRepository.save(folder);
    }
    public Folder updateFolder(Integer numDossier, Folder updatedFolder) {
        // Rechercher le folder existant
        Optional<Folder> existingFolderOptional = folderRepository.findById(numDossier);
        if (existingFolderOptional.isEmpty()) {
            throw new IllegalArgumentException("Folder avec le numéro " + numDossier + " introuvable.");
        }

        Folder existingFolder = existingFolderOptional.get();

        // Mettre à jour les champs nécessaires
        existingFolder.setNomComplet(updatedFolder.getNomComplet());
        existingFolder.setDateNaissance(updatedFolder.getDateNaissance());
        existingFolder.setLieuDeNaissance(updatedFolder.getLieuDeNaissance());
        existingFolder.setSexe(updatedFolder.getSexe());
        existingFolder.setTypePieceIdentite(updatedFolder.getTypePieceIdentite());
        existingFolder.setNumPieceIdentite(updatedFolder.getNumPieceIdentite());
        existingFolder.setAdresse(updatedFolder.getAdresse());
        existingFolder.setNumTel(updatedFolder.getNumTel());
        existingFolder.setEmail(updatedFolder.getEmail());
        existingFolder.setActive(updatedFolder.getActive());
        existingFolder.setFkIdLaboratoire(updatedFolder.getFkIdLaboratoire());

        // Sauvegarder les modifications
        return folderRepository.save(existingFolder);
    }

    public void archiveFolder(Integer numDossier) {
        // Rechercher le folder par son numéro
        Optional<Folder> folderOptional = folderRepository.findById(numDossier);
        if (folderOptional.isEmpty()) {
            throw new IllegalArgumentException("Folder avec le numéro " + numDossier + " introuvable.");
        }

        // Mettre à jour le champ "active"
        Folder folder = folderOptional.get();
        folder.setActive(false);

        // Sauvegarder les modifications
        folderRepository.save(folder);
    }

}

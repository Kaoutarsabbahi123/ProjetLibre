package com.example.FolderService.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numDossier;
    private Date dateCreation;
    private String nomComplet;
    private Date dateNaissance;
    private String lieuDeNaissance;
    private String sexe;
    private String typePieceIdentite;
    private String numPieceIdentite;
    private String adresse;
    private String numTel;
    private String email;
    private Boolean active;
    private Long fkIdLaboratoire;
}

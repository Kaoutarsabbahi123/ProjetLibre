package com.example.MicroTestService.Entity;

import jakarta.persistence.*;


@Entity
public class TestAnalyse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom;
    private String description;
    private String valeursDeReference;
    private Integer idEpreuve;
    private boolean archive;

    // Constructeurs
    public TestAnalyse() {
    }

    public TestAnalyse(Integer id, String nom, String description, String valeursDeReference, Integer idEpreuve, boolean archive) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.valeursDeReference = valeursDeReference;
        this.idEpreuve = idEpreuve;
        this.archive = archive;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValeursDeReference() {
        return valeursDeReference;
    }

    public void setValeursDeReference(String valeursDeReference) {
        this.valeursDeReference = valeursDeReference;
    }

    public Integer getIdEpreuve() {
        return idEpreuve;
    }

    public void setIdEpreuve(Integer idEpreuve) {
        this.idEpreuve = idEpreuve;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}

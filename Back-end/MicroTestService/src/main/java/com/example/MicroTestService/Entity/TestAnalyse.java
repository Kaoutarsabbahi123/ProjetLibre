package com.example.MicroTestService.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TestAnalyse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom;
    private String description;
    @ElementCollection
    @CollectionTable(name = "valeurs_de_reference", joinColumns = @JoinColumn(name = "testanalyse_id"))
    private List<Long> valeursDeReference = new ArrayList<>();
    private Integer idEpreuve;
    private boolean archive;
}

package com.example.AnalysisService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnalyseRequest {
    private Integer id;
    private String nom;
    private String description;
    private Long cout;
    private boolean archive;
    private String nomLaboratoire; // Nom du laboratoire
    private List<EpreuveRequest> epreuves; // Liste des épreuves associées
}

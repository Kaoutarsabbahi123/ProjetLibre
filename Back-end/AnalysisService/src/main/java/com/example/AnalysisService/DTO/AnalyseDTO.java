package com.example.AnalysisService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnalyseDTO {
    private Integer id;
    private String nom;
    private String description;
    private Long cout;
    private boolean archive;
    private String nomLaboratoire; // Nom du laboratoire

}
package com.example.AnalysisService.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TestAnalyseRequest {
    private String nom;
    private String description;
    private String valeursDeReference;
    private Integer idEpreuve;
    private boolean archive;
}
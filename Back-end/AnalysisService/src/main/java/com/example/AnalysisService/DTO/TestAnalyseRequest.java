package com.example.AnalysisService.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TestAnalyseRequest {
    private Integer id;
    private String nom;
    private String description;
    private String valeursDeReference;
    private Integer idEpreuve;
    private boolean archive;
}
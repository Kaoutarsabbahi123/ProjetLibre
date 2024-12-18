package com.example.AnalysisService.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EpreuveRequest {
    private Integer Id;
    private String nom;
    private Integer fkIdAnalyse;
    private boolean archive;
    private List<TestAnalyseRequest> testAnalyses;
}
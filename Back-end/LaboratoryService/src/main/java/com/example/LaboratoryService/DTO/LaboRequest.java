package com.example.LaboratoryService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
public class LaboRequest {
    private String nom;
    private byte[] logo;
    private String nrc;
    private boolean active;
    private LocalDate dateActivation;

    private String numTel;
    private String fax;
    private String email;
    private List<AdressRequest> adresses;


    // Getters et setters
}
package com.example.LaboratoryService.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numTel;    // Numéro de téléphone du contact
    private String fax;       // Numéro de fax du contact
    private String email;     // Email du contact
    private Long fkIdLaboratoire;
    private List<AdressRequest> adresses;

}

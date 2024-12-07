package com.example.LaboratoryService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequest {
   private Long id;
    private String numTel;    // Numéro de téléphone du contact
    private String fax;       // Numéro de fax du contact
    private String email;     // Email du contact
    private Long fkIdLaboratoire;
// ID du laboratoire auquel le contact est associé

    // Ajouter des annotations de validation si nécessaire, par exemple @Email ou @NotNull
}

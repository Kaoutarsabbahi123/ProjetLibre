package com.example.AuthService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private String password;
    private String email;
    private String nomComplet;
    private String numTel;
    private String role;
    private Integer fkIdLaboratoire;
    private boolean isActive; // Add the isActive field

    // Constructor for email and nomComplet
    public UserResponse(String email, String nomComplet) {
        this.email = email;
        this.nomComplet = nomComplet;
    }
}

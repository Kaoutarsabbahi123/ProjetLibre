package com.example.UserService.DTO;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor@NoArgsConstructor
public class UserResponse {
    private String email;
    private String nomComplet;
    private String numTel;
    private String role;
    private Integer fkIdLaboratoire;
    private String nomLaboratoire;
    private boolean active;
}

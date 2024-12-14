package com.example.UserService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
    @Id
    private String email;
    private String password;
    private String nomComplet;
    private String numTel;
    private String role;
    private Integer fkIdLaboratoire;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

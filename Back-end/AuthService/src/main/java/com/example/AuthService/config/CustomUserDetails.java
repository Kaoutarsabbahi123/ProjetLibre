package com.example.AuthService.config;

import com.example.AuthService.dto.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String password;
    private UserResponse userResponse;


    public CustomUserDetails(UserResponse userResponse) {
        this.email = userResponse.getEmail();  // Utiliser email de UserResponse
        this.password = userResponse.getPassword();  // Utiliser mot de passe de UserResponse
        this.userResponse = userResponse;  // Conserver l'objet UserResponse entier
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { // Spring Security attend toujours une méthode appelée "getUsername()"
        return email; // Retournez l'email ici
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }
}
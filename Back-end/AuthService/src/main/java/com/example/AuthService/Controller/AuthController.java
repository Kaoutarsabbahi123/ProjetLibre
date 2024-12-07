package com.example.AuthService.Controller;


import com.example.AuthService.config.CustomUserDetails;
import com.example.AuthService.dto.AuthRequest;
import com.example.AuthService.Service.AuthService;
import com.example.AuthService.dto.UserResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/auth")
@Setter
public class AuthController {

    @Autowired
    private AuthService service;

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> getToken(@RequestBody AuthRequest authRequest) {
        System.out.println("Attempting login for email: " + authRequest.getEmail());

        try {
            // Authentifier l'utilisateur avec ses informations (email et mot de passe)
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            System.out.println("Authentication successful for email: " + authRequest.getEmail());

            // Générer le token JWT
            String token = service.generateToken(authRequest.getEmail());
            System.out.println("JWT token generated successfully: " + token);

            // Utiliser CustomUserDetails pour récupérer les informations de l'utilisateur déjà chargées
            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
            UserResponse userResponse = userDetails.getUserResponse();
            System.out.println("User details retrieved: " + userResponse);

            // Créer une réponse contenant le token et les informations de l'utilisateur
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        System.out.println("Validating token: " + token);

        try {
            service.validateToken(token);
            System.out.println("Token is valid");
            return "Token is valid";
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid token");
        }
    }
}

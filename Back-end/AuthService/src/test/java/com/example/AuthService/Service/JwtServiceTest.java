package com.example.AuthService.Service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private JwtService jwtService = new JwtService();  // Instance de JwtService à tester

    @Test
    public void testGenerateToken() {
        // Arrange
        String userName = "test@example.com";

        // Act
        String token = jwtService.generateToken(userName);

        // Assert
        assertNotNull(token, "The generated token should not be null.");
    }

    @Test
    public void testValidateToken() {
        // Arrange
        String token = jwtService.generateToken("test@example.com");

        // Act & Assert
        assertDoesNotThrow(() -> jwtService.validateToken(token), "The token should be valid.");
    }
}

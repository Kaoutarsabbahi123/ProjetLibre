package com.example.AuthService.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class AuthServiceTest {

    @Mock
    private JwtService jwtService;  // Mock de JwtService pour tester AuthService

    @InjectMocks
    private AuthService authService;  // Injecte JwtService mocké dans AuthService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateToken() {
        // Arrange
        String email = "test@example.com";
        String expectedToken = "mockedToken";
        Mockito.when(jwtService.generateToken(email)).thenReturn(expectedToken);

        // Act
        String token = authService.generateToken(email);

        // Assert
        assertEquals(expectedToken, token, "The generated token should match the expected value.");
    }

    @Test
    public void testValidateToken() {
        // Arrange
        String token = "validToken";

        // Act
        authService.validateToken(token);

        // Assert
        verify(jwtService).validateToken(token);  // Vérifier que validateToken a bien été appelé
    }
}

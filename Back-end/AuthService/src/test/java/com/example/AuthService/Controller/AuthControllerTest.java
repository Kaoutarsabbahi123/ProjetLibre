package com.example.AuthService.Controller;

import com.example.AuthService.Service.AuthService;
import com.example.AuthService.config.CustomUserDetails;
import com.example.AuthService.dto.AuthRequest;
import com.example.AuthService.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authenticationManager);
        authController.setService(authService);
    }

    @Test
    void testGetToken_Success() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("test@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserResponse()).thenReturn(userResponse);
        when(authService.generateToken(authRequest.getEmail())).thenReturn("mockToken");

        // Act
        ResponseEntity<Map<String, Object>> response = authController.getToken(authRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assert responseBody != null;
        assertEquals("mockToken", responseBody.get("token"));
        assertEquals(userResponse, responseBody.get("user"));

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, times(1)).generateToken(authRequest.getEmail());
    }

    @Test
    void testGetToken_InvalidCredentials() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationServiceException("Invalid credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.getToken(authRequest));

        // Verify no token generation
        verify(authService, never()).generateToken(anyString());
    }
}

package com.example.UserService.Controller;

import com.example.UserService.Service.UserService;
import com.example.UserService.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc pour effectuer des requêtes HTTP simulées

    @Mock
    private UserService userService;  // Nous simulons le service UserService

    @InjectMocks
    private UserController userController;  // Injecte userService dans userController

    @Autowired
    private ObjectMapper objectMapper;  // Injection de l'ObjectMapper pour la sérialisation JSON

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper(); // Optionally initialize a real instance
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUserByEmail_ValidEmail() throws Exception {
        // Arrange
        User user = new User("test@example.com", "password123");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/users/email/test@example.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"));
    }

    @Test
    public void testGetUserByEmail_InvalidEmail() throws Exception {
        // Arrange
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/users/email/nonexistent@example.com"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateUser() throws Exception {
        // Arrange
        User user = new User("newuser@example.com", "password456");
        when(userService.saveUser(user)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))  // Sérialise l'objet en JSON
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("newuser@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password456"));
    }
}
package com.example.UserService.Repository;

import com.example.UserService.entity.User;
import com.example.UserService.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Cette annotation configure une base de données H2 en mémoire pour les tests
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository; // Nous injectons le repository pour l'utiliser

    @Test
    public void findByEmail_ValidEmail_ReturnsUser() {
        // Arrange
        User user = new User("test@example.com", "password123");
        userRepository.save(user); // Sauvegarder l'utilisateur dans la base de données H2

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("test@example.com", foundUser.get().getEmail(), "Email should match");
    }

    @Test
    public void findByEmail_InvalidEmail_ReturnsEmpty() {
        // Arrange: Aucun utilisateur n'est sauvegardé pour cet email

        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundUser.isPresent(), "User should not be found for this email");
    }

    @Test
    public void saveUser_SavesCorrectly() {
        // Arrange
        User user = new User("save@example.com", "password123");

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getEmail(), "User should have an email");
        assertEquals("save@example.com", savedUser.getEmail(), "Email should match the saved email");
        assertEquals("password123", savedUser.getPassword(), "Password should match the saved password");
    }


}


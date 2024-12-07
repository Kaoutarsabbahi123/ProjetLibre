package com.example.UserService.Service;

import com.example.UserService.entity.User;
import com.example.UserService.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService; // Classe que nous testons

    @Mock
    private UserRepository userRepository; // Dépendance mockée

    @Mock
    private BCryptPasswordEncoder passwordEncoder; // Dépendance mockée

    @Test
    public void findByEmail_UserExists_ReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(email, result.get().getEmail());
    }

    @Test
    public void findByEmail_UserDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        String email = "nonexistent@example.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void saveUser_ValidUser_ReturnsSavedUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");

        String encodedPassword = "encodedPassword";
        Mockito.when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(encodedPassword, savedUser.getPassword());
        Mockito.verify(userRepository, Mockito.times(1)).save(user); // Vérifie que save() est appelé
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("plainPassword"); // Vérifie que encode() est appelé
    }

}


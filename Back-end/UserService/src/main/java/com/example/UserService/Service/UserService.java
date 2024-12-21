package com.example.UserService.Service;


import com.example.UserService.DTO.UserProfileDTO;
import com.example.UserService.entity.User;
import com.example.UserService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        // Vérifier si l'email existe déjà en utilisant findByEmail
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà !");
        }

        // Encoder le mot de passe avant de sauvegarder
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User updateUserProfile(String email, UserProfileDTO updatedProfile) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNomComplet(updatedProfile.getNomComplet());
        user.setNumTel(updatedProfile.getNumTel());
        return userRepository.save(user);
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    public User updateUser(String email, User updatedUser) {
        // Rechercher l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Mise à jour des champs
        if (updatedUser.getNomComplet() != null) {
            user.setNomComplet(updatedUser.getNomComplet());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getNumTel() != null) {
            user.setNumTel(updatedUser.getNumTel());
        }
        if (updatedUser.getFkIdLaboratoire() != null) {
            user.setFkIdLaboratoire(updatedUser.getFkIdLaboratoire());
        }
        if (updatedUser.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }


    public boolean changeUserStatus(String email, boolean isActive) {
        // Rechercher l'utilisateur par email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(isActive); // Modifier le statut
            userRepository.save(user);
            return true;
        }
        return false; // L'utilisateur avec cet email n'existe pas
    }

}

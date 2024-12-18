package com.example.UserService.Controller;

import com.example.UserService.DTO.UserProfileDTO;
import com.example.UserService.DTO.UserResponse;
import com.example.UserService.Service.UserService;
import com.example.UserService.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private RestTemplate restTemplate;

    private String laboServiceUrl = "http://localhost:8886/api/laboratoires";
    public UserController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }// URL de base

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@RequestParam("email") String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO userProfile = new UserProfileDTO();
        userProfile.setEmail(user.getEmail());
        userProfile.setNomComplet(user.getNomComplet());
        userProfile.setNumTel(user.getNumTel());
        userProfile.setRole(user.getRole());

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @RequestParam("email") String email,
            @RequestBody UserProfileDTO updatedProfile) {

        User updatedUser = userService.updateUserProfile(email, updatedProfile);

        UserProfileDTO response = new UserProfileDTO();
        response.setEmail(updatedUser.getEmail());
        response.setNomComplet(updatedUser.getNomComplet());
        response.setNumTel(updatedUser.getNumTel());
        response.setRole(updatedUser.getRole());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @RequestParam("email") String email,
            @RequestBody Map<String, String> passwordRequest) {

        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");

        userService.updatePassword(email, oldPassword, newPassword);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();

        List<UserResponse> userResponses = users.stream().map(user -> {
            UserResponse response = new UserResponse();
            response.setEmail(user.getEmail());
            response.setNomComplet(user.getNomComplet());
            response.setNumTel(user.getNumTel());
            response.setRole(user.getRole());
            response.setFkIdLaboratoire(user.getFkIdLaboratoire());
            response.setActive(user.getActive());

            // Dynamique : ajout de /nom/{id} à l'URL pour récupérer le nom du laboratoire
            try {
                String laboratoireUrl = laboServiceUrl + "/nom/" + user.getFkIdLaboratoire(); // Construction de l'URL complète
                String laboratoireNom = restTemplate.getForObject(laboratoireUrl, String.class); // Appel au service labo
                response.setNomLaboratoire(laboratoireNom);  // Ajoutez le nom du labo au UserResponse
            } catch (Exception e) {
                response.setNomLaboratoire("Laboratoire non trouvé");  // Gestion des erreurs si le labo n'est pas trouvé
            }

            return response;
        }).toList();

        return ResponseEntity.ok(userResponses);
    }
}

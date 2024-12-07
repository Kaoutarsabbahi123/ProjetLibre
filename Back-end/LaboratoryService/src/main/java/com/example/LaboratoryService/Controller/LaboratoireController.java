package com.example.LaboratoryService.Controller;

import com.example.LaboratoryService.DTO.ContactRequest;
import com.example.LaboratoryService.DTO.LaboRequest;
import com.example.LaboratoryService.DTO.AdressRequest;
import com.example.LaboratoryService.Entity.Laboratoire;
import com.example.LaboratoryService.Service.LaboratoireService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/laboratoires")
public class LaboratoireController {

    private final LaboratoireService laboratoireService;
    private final RestTemplate restTemplate;

    // URL des services récupérées depuis la configuration
    private String contactServiceUrl="http://localhost:8885";
    private String adresseServiceUrl="http://localhost:8084";

    public LaboratoireController(LaboratoireService laboratoireService, RestTemplate restTemplate) {
        this.laboratoireService = laboratoireService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/createWithDetails")
    public ResponseEntity<?> createLaboratoireWithDetails(
            @RequestParam("logo") MultipartFile logoFile,
            @RequestParam("nom") String nom,
            @RequestParam("nrc") String nrc,
            @RequestParam("active") boolean active,
            @RequestParam("dateActivation") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateActivation,
            @RequestParam("numTel") String numTel,
            @RequestParam("fax") String fax,
            @RequestParam("email") String email,
            @RequestParam(value = "adresses", required = false) List<AdressRequest> adresses) throws IOException {

        // Valider le fichier logo
        if (logoFile == null || logoFile.isEmpty() || !isValidImage(logoFile)) {
            return ResponseEntity.badRequest().body("Invalid logo file. Please upload a valid image.");
        }

        // Création du laboratoire
        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setNom(nom);
        laboratoire.setLogo(logoFile.getBytes());
        laboratoire.setNrc(nrc);
        laboratoire.setActive(active);
        laboratoire.setDateActivation(dateActivation);

        Laboratoire savedLaboratoire = laboratoireService.saveLaboratoire(laboratoire);

        // Création du contact
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFkIdLaboratoire(savedLaboratoire.getId());
        contactRequest.setNumTel(numTel);
        contactRequest.setFax(fax);
        contactRequest.setEmail(email);

        ResponseEntity<ContactRequest> contactResponse = restTemplate.postForEntity(
                contactServiceUrl + "/api/contacts", contactRequest, ContactRequest.class
        );

        if (!contactResponse.getStatusCode().is2xxSuccessful() || contactResponse.getBody() == null) {
            return ResponseEntity.status(contactResponse.getStatusCode())
                    .body("Error while creating contact. Contact Service returned: " + contactResponse.getStatusCode());
        }

        Long contactId = contactResponse.getBody().getId();

        // Création des adresses
        if (adresses != null && !adresses.isEmpty()) {
            adresses.forEach(adresseRequest -> {
                adresseRequest.setFkIdcontact(contactId);
                ResponseEntity<AdressRequest> addressResponse = restTemplate.postForEntity(
                        adresseServiceUrl + "/api/addresses", adresseRequest, AdressRequest.class
                );
                if (!addressResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Failed to create address: " + addressResponse.getStatusCode());
                }
            });
        }

        return ResponseEntity.ok(savedLaboratoire);
    }

    // Valider si le fichier est une image
    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }

    // Autres méthodes pour le laboratoire
}

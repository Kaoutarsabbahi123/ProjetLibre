package com.example.LaboratoryService.Controller;

import com.example.LaboratoryService.DTO.ContactRequest;
import com.example.LaboratoryService.DTO.AdressRequest;
import com.example.LaboratoryService.DTO.LaboRequest;
import com.example.LaboratoryService.Entity.Laboratoire;
import com.example.LaboratoryService.Service.LaboratoireService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/laboratoires")
public class LaboratoireController {

    private final LaboratoireService laboratoireService;
    private final RestTemplate restTemplate;

    private String contactServiceUrl = "http://localhost:8885";
    private String adresseServiceUrl = "http://localhost:8084";

    public LaboratoireController(LaboratoireService laboratoireService, RestTemplate restTemplate) {
        this.laboratoireService = laboratoireService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/createlabo")
    public ResponseEntity<Laboratoire> createLaboratoire(
            @RequestParam("nom") String nom,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("nrc") String nrc,
            @RequestParam(value = "active", defaultValue = "true") boolean active,
            @RequestParam("dateActivation") LocalDate dateActivation,
            @RequestParam(value = "contacts", required = false) String contactsJson // Recevoir le JSON sous forme de chaîne
    ) throws IOException {
        // Vérification de la présence du nom
        if (nom == null || nom.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Nom obligatoire
        }

        try {
            // Créer un objet Laboratoire avec les données fournies
            Laboratoire labo = new Laboratoire();
            labo.setNom(nom);

            // Si un logo est fourni, on le convertit en tableau d'octets
            if (logo != null && !logo.isEmpty()) {
                labo.setLogo(logo.getBytes());
            }

            labo.setNrc(nrc);
            labo.setActive(active);
            labo.setDateActivation(dateActivation != null ? dateActivation : LocalDate.now());

            // Sauvegarder le laboratoire via le service
            Laboratoire savedLaboratoire = laboratoireService.saveLaboratoire(labo);

            // Vérification si des contacts sont fournis
            if (contactsJson != null && !contactsJson.isEmpty()) {
                // Conversion du JSON des contacts en objets
                ObjectMapper objectMapper = new ObjectMapper();
                List<ContactRequest> contacts = objectMapper.readValue(contactsJson, new TypeReference<List<ContactRequest>>() {
                });

                // Création des contacts associés au laboratoire
                for (ContactRequest contactRequest : contacts) {
                    contactRequest.setFkIdLaboratoire(savedLaboratoire.getId());

                    // Sauvegarder le contact via un service ou une API externe
                    ResponseEntity<ContactRequest> contactResponse = restTemplate.postForEntity(
                            contactServiceUrl + "/api/contacts", contactRequest, ContactRequest.class
                    );

                    // Récupérer l'ID du contact créé
                    Long contactId = contactResponse.getBody().getId();

                    // Création des adresses associées à chaque contact
                    List<AdressRequest> adresses = contactRequest.getAdresses();
                    if (adresses != null && !adresses.isEmpty()) {
                        for (AdressRequest adresseRequest : adresses) {
                            adresseRequest.setFkIdcontact(contactId);

                            // Sauvegarder chaque adresse via un service ou une API externe
                            ResponseEntity<AdressRequest> addressResponse = restTemplate.postForEntity(
                                    adresseServiceUrl + "/api/addresses", adresseRequest, AdressRequest.class
                            );
                        }
                    }
                }
            }

            return ResponseEntity.ok(savedLaboratoire);

        } catch (JsonProcessingException e) {
            System.err.println("Erreur lors de la conversion des contacts JSON : " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            System.err.println("Erreur lors du traitement du fichier logo : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping
    public ResponseEntity<List<Laboratoire>> getAllLaboratoires() {
        List<Laboratoire> laboratoires = laboratoireService.getAllLaboratoires();

        // Mapper les laboratoires en DTO avec email obtenu via le service Contact
        List<Laboratoire> labos = laboratoires.stream()
                .map(labo -> {
                    return new Laboratoire(
                            labo.getId(),
                            labo.getNom(),
                            labo.getLogo(),
                            labo.getNrc(),
                            labo.isActive(),
                            labo.getDateActivation()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(labos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboRequest> getLaboratoireById(@PathVariable Long id) {
        try {
            // Récupérer le laboratoire depuis la base de données
            Optional<Laboratoire> laboOptional = laboratoireService.getLaboratoireById(id);

            // Vérifier si le laboratoire est présent dans l'Optional
            if (!laboOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Laboratoire non trouvé
            }

            Laboratoire labo = laboOptional.get(); // Extraire le laboratoire de l'Optional

            // Récupérer les informations de contact via le service Contact
            ResponseEntity<List<ContactRequest>> contactResponse = restTemplate.exchange(
                    contactServiceUrl + "/api/contacts/laboratoire/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ContactRequest>>() {}
            );

            // Gérer le cas où aucun contact n'est trouvé
            List<ContactRequest> contactList = contactResponse.getBody();
            if (contactList == null) {
                contactList = new ArrayList<>();
            }

            // Pour chaque contact, récupérer ses adresses
            for (ContactRequest contact : contactList) {
                ResponseEntity<List<AdressRequest>> addressResponse = restTemplate.exchange(
                        adresseServiceUrl + "/api/addresses/contact/" + contact.getId(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<AdressRequest>>() {}
                );
                // Ajouter les adresses au contact
                List<AdressRequest> addresses = addressResponse.getBody();
                contact.setAdresses(addresses != null ? addresses : new ArrayList<>());
            }

            // Créer un DTO pour encapsuler les informations du laboratoire, des contacts et des adresses
            LaboRequest laboratoireDTO = new LaboRequest(
                    labo.getNom(),
                    labo.getLogo(),
                    labo.getNrc(),
                    labo.isActive(),
                    labo.getDateActivation(),
                    contactList // Liste des contacts avec leurs adresses
            );

            return ResponseEntity.ok(laboratoireDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur interne
        }
    }



}



   /* @PutMapping("/{id}")
    public ResponseEntity<LaboRequest> updateLaboratoire(@PathVariable Long id, @RequestBody LaboRequest laboRequest) {
        try {
            // Récupérer le laboratoire depuis la base de données
            Optional<Laboratoire> laboOptional = laboratoireService.getLaboratoireById(id);

            // Vérifier si le laboratoire est présent dans l'Optional
            if (!laboOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Laboratoire non trouvé
            }

            Laboratoire labo = laboOptional.get(); // Extraire le laboratoire de l'Optional

            // Mettre à jour les informations du laboratoire
            labo.setNom(laboRequest.getNom());
            labo.setLogo(laboRequest.getLogo());
            labo.setNrc(laboRequest.getNrc());
            labo.setActive(laboRequest.isActive());
            labo.setDateActivation(laboRequest.getDateActivation());

            // Sauvegarder les modifications du laboratoire
            laboratoireService.saveLaboratoire(labo);

            // Récupérer les informations de contact via le service Contact
            ResponseEntity<List<ContactRequest>> contactResponse = restTemplate.exchange(
                    contactServiceUrl + "/api/contacts/laboratoire/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ContactRequest>>() {}
            );

            // Gérer le cas où aucun contact n'est trouvé
            List<ContactRequest> contactList = contactResponse.getBody();
            ContactRequest contact = (contactList != null && !contactList.isEmpty()) ? contactList.get(0) : null;

            // Mettre à jour ou créer un nouveau contact si nécessaire
            if (contact != null) {
                contact.setEmail(laboRequest.getEmail());
                contact.setNumTel(laboRequest.getNumTel());
                contact.setFax(laboRequest.getFax());

                // Effectuer la mise à jour du contact via le service Contact
                restTemplate.put(contactServiceUrl + "/api/contacts/" + contact.getId(), contact);
            } else {
                // Si aucun contact n'existe, créer un nouveau contact
                ContactRequest newContact = new ContactRequest();
                newContact.setFkIdLaboratoire(labo.getId());
                newContact.setEmail(laboRequest.getEmail());
                newContact.setNumTel(laboRequest.getNumTel());
                newContact.setFax(laboRequest.getFax());

                restTemplate.postForEntity(contactServiceUrl + "/api/contacts", newContact, ContactRequest.class);
            }

            // Récupérer et mettre à jour les adresses associées au contact via le service Address
            if (contact != null) {
                ResponseEntity<List<AdressRequest>> addressResponse = restTemplate.exchange(
                        adresseServiceUrl + "/api/addresses/contact/" + contact.getId(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<AdressRequest>>() {}
                );

                List<AdressRequest> addresses = addressResponse.getBody();

                // Mettre à jour les adresses existantes ou ajouter des nouvelles adresses
                if (addresses != null) {
                    for (AdressRequest address : addresses) {
                        // Vous pouvez mettre à jour les adresses ici selon la logique de votre application
                        // Exemple : On garde les adresses reçues comme il faut ou on peut les remplacer.
                    }
                }

                // Ajouter des nouvelles adresses si elles existent dans la requête
                if (laboRequest.getAdresses() != null && !laboRequest.getAdresses().isEmpty()) {
                    for (AdressRequest addressRequest : laboRequest.getAdresses()) {
                        addressRequest.setFkIdcontact(contact.getId());
                        restTemplate.postForEntity(adresseServiceUrl + "/api/addresses", addressRequest, AdressRequest.class);
                    }
                }
            }

            // Créer un DTO pour encapsuler les informations mises à jour du laboratoire, du contact et des adresses
            LaboRequest updatedLaboratoireDTO = new LaboRequest(
                    labo.getNom(),
                    labo.getLogo(),
                    labo.getNrc(),
                    labo.isActive(),
                    labo.getDateActivation(),
                    laboRequest.getEmail(), // Email mis à jour
                    laboRequest.getNumTel(), // Téléphone mis à jour
                    laboRequest.getFax(), // Fax mis à jour
                    laboRequest.getAdresses() // Liste des adresses mises à jour
            );

            return ResponseEntity.ok(updatedLaboratoireDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur interne
        }
    }*/




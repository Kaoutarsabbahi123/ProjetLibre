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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
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
    @PutMapping("/update/{id}")
    public ResponseEntity<Laboratoire> updateLaboratoireWithContactsAndAddresses(
            @PathVariable Long id,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "nrc", required = false) String nrc,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "dateActivation", required = false) LocalDate dateActivation,
            @RequestParam(value = "contacts", required = false) String contactsJson // Recevoir le JSON des contacts
    ) throws IOException {
        try {
            System.out.println("Contacts reçus : " + contactsJson);

            // Vérifier si le laboratoire existe
            Optional<Laboratoire> laboOptional = laboratoireService.getLaboratoireById(id);
            if (!laboOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Laboratoire non trouvé
            }

            Laboratoire labo = laboOptional.get(); // Extraire le laboratoire de l'Optional

            // Modifier les propriétés du laboratoire si elles sont fournies
            if (nom != null && !nom.isEmpty()) {
                labo.setNom(nom);
            }
            if (nrc != null && !nrc.isEmpty()) {
                labo.setNrc(nrc);
            }
            if (active != null) {
                labo.setActive(active);
            }
            if (dateActivation != null) {
                labo.setDateActivation(dateActivation);
            }

            // Si un nouveau logo est fourni, on le remplace
            if (logo != null && !logo.isEmpty()) {
                labo.setLogo(logo.getBytes());
            }

            // Sauvegarder les modifications du laboratoire
            Laboratoire updatedLaboratoire = laboratoireService.saveLaboratoire(labo);

            // Si des contacts sont fournis dans le JSON, on les met à jour
            if (contactsJson != null && !contactsJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<ContactRequest> contacts = objectMapper.readValue(contactsJson, new TypeReference<List<ContactRequest>>() {});

                for (ContactRequest contactRequest : contacts) {
                    if (contactRequest.getId() != null) {
                        // Si l'ID du contact est présent, on effectue une mise à jour
                        ResponseEntity<ContactRequest> contactResponse = restTemplate.exchange(
                                contactServiceUrl + "/api/contacts/" + contactRequest.getId(),
                                HttpMethod.GET,
                                null,
                                ContactRequest.class
                        );

                        ContactRequest existingContact = contactResponse.getBody();
                        if (existingContact != null) {
                            // Mettre à jour le contact
                            existingContact.setNumTel(contactRequest.getNumTel());
                            existingContact.setEmail(contactRequest.getEmail());
                            existingContact.setFax(contactRequest.getFax());
                            restTemplate.put(contactServiceUrl + "/api/contacts/" + existingContact.getId(), existingContact);
                        } else {
                            // Si le contact n'existe pas, on le crée
                            contactRequest.setFkIdLaboratoire(updatedLaboratoire.getId());
                            ResponseEntity<ContactRequest> newContactResponse = restTemplate.postForEntity(contactServiceUrl + "/api/contacts", contactRequest, ContactRequest.class);
                            contactRequest.setId(newContactResponse.getBody().getId()); // Récupérer l'ID du contact créé
                        }
                    } else {
                        // Si l'ID est null, on crée un nouveau contact
                        contactRequest.setFkIdLaboratoire(updatedLaboratoire.getId());
                        ResponseEntity<ContactRequest> newContactResponse = restTemplate.postForEntity(contactServiceUrl + "/api/contacts", contactRequest, ContactRequest.class);
                        contactRequest.setId(newContactResponse.getBody().getId()); // Récupérer l'ID du contact créé
                    }

                    // Traitez ensuite les adresses
                    List<AdressRequest> addresses = contactRequest.getAdresses();
                    if (addresses != null && !addresses.isEmpty()) {
                        for (AdressRequest addressRequest : addresses) {
                            if (addressRequest.getId() != null) {
                                // Vérifier si l'adresse existe et la mettre à jour si nécessaire
                                ResponseEntity<AdressRequest> addressResponse = restTemplate.exchange(
                                        adresseServiceUrl + "/api/addresses/" + addressRequest.getId(),
                                        HttpMethod.GET,
                                        null,
                                        AdressRequest.class
                                );

                                AdressRequest existingAddress = addressResponse.getBody();
                                if (existingAddress != null) {
                                    // Mettre à jour l'adresse
                                    restTemplate.put(adresseServiceUrl + "/api/addresses/" + existingAddress.getId(), addressRequest);
                                } else {
                                    // Créer une nouvelle adresse
                                    addressRequest.setFkIdcontact(contactRequest.getId());
                                    restTemplate.postForEntity(adresseServiceUrl + "/api/addresses", addressRequest, AdressRequest.class);
                                }
                            } else {
                                // Si l'adresse n'a pas d'ID, créer une nouvelle adresse
                                addressRequest.setFkIdcontact(contactRequest.getId());
                                restTemplate.postForEntity(adresseServiceUrl + "/api/addresses", addressRequest, AdressRequest.class);
                            }
                        }
                    }
                }
            }

            return ResponseEntity.ok(updatedLaboratoire); // Retourner le laboratoire mis à jour

        } catch (IOException e) {
            System.err.println("Erreur lors du traitement du fichier logo ou du JSON : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/archive/{id}")
    public ResponseEntity<String> archiveLaboratoire(@PathVariable Long id) {
        try {
            // Vérifier si le laboratoire existe
            Optional<Laboratoire> laboOptional = laboratoireService.getLaboratoireById(id);
            if (!laboOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Laboratoire non trouvé");
            }

            // Archiver le laboratoire en mettant `active` à false
            Laboratoire laboratoire = laboOptional.get();
            laboratoire.setActive(false);
            laboratoireService.saveLaboratoire(laboratoire);

            return ResponseEntity.ok("Laboratoire archivé avec succès");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'archivage du laboratoire : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'archivage du laboratoire");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Laboratoire>> searchLaboratoires(@RequestParam("keyword") String keyword) {
        try {
            // Rechercher les laboratoires par mot-clé
            List<Laboratoire> foundLaboratoires = laboratoireService.searchLaboratoiresByKeyword(keyword);

            if (foundLaboratoires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Aucun laboratoire trouvé
            }

            return ResponseEntity.ok(foundLaboratoires);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur interne
        }
    }
    @GetMapping("/noms")
    public ResponseEntity<List<Object[]>> getAllLaboratoiresNom() {
        try {
            // Récupérer tous les laboratoires
            List<Laboratoire> laboratoires = laboratoireService.getAllLaboratoires();

            // Créer une liste de tableaux contenant l'ID et le nom des laboratoires
            List<Object[]> laboratoiresInfo = laboratoires.stream()
                    .map(labo -> new Object[]{labo.getId(), labo.getNom()})
                    .collect(Collectors.toList());

            // Retourner la liste des laboratoires avec ID et nom
            return ResponseEntity.ok(laboratoiresInfo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/nom/{id}")
    public ResponseEntity<String> getNomLaboratoire(@PathVariable Long id) {
        String nom = laboratoireService.getNomLaboratoireById(id);
        return ResponseEntity.ok(nom);
    }

}





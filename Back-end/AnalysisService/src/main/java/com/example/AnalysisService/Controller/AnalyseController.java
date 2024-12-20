package com.example.AnalysisService.Controller;

import com.example.AnalysisService.DTO.AnalyseDTO;
import com.example.AnalysisService.DTO.AnalyseRequest;
import com.example.AnalysisService.DTO.EpreuveRequest;
import com.example.AnalysisService.DTO.TestAnalyseRequest;
import com.example.AnalysisService.Entity.Analyse;
import com.example.AnalysisService.Service.AnalyseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/analyses")
public class AnalyseController {
    private String testAnalyseServiceUrl = "http://localhost:8087/testanalyses";
    private String EpreuveServiceUrl = "http://localhost:8889/epreuves";
    private String LaboratoireServiceUrl = "http://localhost:8886/api/laboratoires";
    private final AnalyseService analyseService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public AnalyseController(AnalyseService analyseService, RestTemplate restTemplate) {
        this.analyseService = analyseService;
        this.restTemplate = restTemplate;
    }
    @PostMapping("/create")
    public ResponseEntity<Analyse> createAnalyse(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("cout") Long cout,
            @RequestParam("fkIdLaboratoire") Long fkIdLaboratoire, // ID directement sélectionné depuis le formulaire
            @RequestParam("epreuves") String epreuvesJson // JSON pour les épreuves
    ) throws JsonProcessingException {
        try {
            // Création de l'analyse
            Analyse analyse = new Analyse();
            analyse.setNom(nom);
            analyse.setDescription(description);
            analyse.setCout(cout);
            analyse.setFkIdLaboratoire(fkIdLaboratoire); // Utilisation directe de l'ID
            analyse.setArchive(false);

            Analyse savedAnalyse = analyseService.saveAnalyse(analyse);

            // Traitement des épreuves depuis le JSON
            List<EpreuveRequest> epreuves = objectMapper.readValue(epreuvesJson, new TypeReference<List<EpreuveRequest>>() {});

            for (EpreuveRequest epreuveRequest : epreuves) {
                // Création de l'épreuve via l'API externe
                EpreuveRequest epreuveToSave = new EpreuveRequest();
                epreuveToSave.setNom(epreuveRequest.getNom());
                epreuveToSave.setFkIdAnalyse(savedAnalyse.getId());
                epreuveToSave.setArchive(false);

                ResponseEntity<EpreuveRequest> epreuveResponse = restTemplate.postForEntity(
                        EpreuveServiceUrl+ "/create", epreuveToSave, EpreuveRequest.class
                );

                EpreuveRequest savedEpreuve = epreuveResponse.getBody();

                // Traitement des testanalyses pour chaque épreuve
                List<TestAnalyseRequest> testAnalyses = epreuveRequest.getTestAnalyses();
                for (TestAnalyseRequest testAnalyseRequest : testAnalyses) {
                    TestAnalyseRequest testAnalyseToSave = new TestAnalyseRequest();
                    testAnalyseToSave.setNom(testAnalyseRequest.getNom());
                    testAnalyseToSave.setDescription(testAnalyseRequest.getDescription());
                    testAnalyseToSave.setValeursDeReference(testAnalyseRequest.getValeursDeReference());
                    testAnalyseToSave.setIdEpreuve(savedEpreuve.getId());
                    testAnalyseToSave.setArchive(false);

                    // Envoi du testanalyse au microservice testanalyse
                    restTemplate.postForEntity(
                            testAnalyseServiceUrl + "/create", testAnalyseToSave, TestAnalyseRequest.class
                    );
                }
            }

            return ResponseEntity.ok(savedAnalyse);

        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'analyse : " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<AnalyseDTO>> getAllAnalyses() {
        List<Analyse> analyses = analyseService.getAllAnalyses();

        // Mapper les analyses et récupérer le nom du laboratoire
        List<AnalyseDTO> analyseDTOs = analyses.stream()
                .map(analyse -> {
                    // Appel à LaboratoryService pour récupérer le nom
                    String nomLaboratoire = restTemplate.getForObject(LaboratoireServiceUrl + "/nom/"+ analyse.getFkIdLaboratoire(), String.class);;

                    // Créer un DTO avec les données nécessaires
                    return new AnalyseDTO(
                            analyse.getId(),
                            analyse.getNom(),
                            analyse.getDescription(),
                            analyse.getCout(),
                            analyse.isArchive(),
                            nomLaboratoire
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(analyseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyseRequest> getAnalyseDetails(@PathVariable Integer id) {
        try {
            // Retrieve the Analyse entity from the database
            Optional<Analyse> optionalAnalyse = analyseService.getAnalyseById(id);

            if (optionalAnalyse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Analyse analyse = optionalAnalyse.get();

            // Retrieve associated Epreuves from the external service
            ResponseEntity<List<EpreuveRequest>> epreuveResponse = restTemplate.exchange(
                    EpreuveServiceUrl + "/analyse/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EpreuveRequest>>() {}
            );

            List<EpreuveRequest> epreuves = epreuveResponse.getBody();
            if (epreuves == null) {
                epreuves = List.of();
            }

            // For each Epreuve, retrieve associated TestAnalyses
            for (EpreuveRequest epreuve : epreuves) {
                ResponseEntity<List<TestAnalyseRequest>> testResponse = restTemplate.exchange(
                        testAnalyseServiceUrl + "/epreuve/" + epreuve.getId(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TestAnalyseRequest>>() {}
                );
                List<TestAnalyseRequest> tests = testResponse.getBody();
                epreuve.setTestAnalyses(tests != null ? tests : List.of());
            }

            // Call LaboratoryService to retrieve the laboratory name
            String nomLaboratoire = restTemplate.getForObject(
                    LaboratoireServiceUrl + "/nom/" + analyse.getFkIdLaboratoire(),
                    String.class
            );

            // Create the AnalyseRequest object
            AnalyseRequest analyseRequest = new AnalyseRequest(
                    analyse.getId(),
                    analyse.getNom(),
                    analyse.getDescription(),
                    analyse.getCout(),
                    analyse.isArchive(),
                    nomLaboratoire,
                    epreuves
            );

            return ResponseEntity.ok(analyseRequest);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Analyse> updateAnalyseWithEpreuvesAndTestAnalyses(
            @PathVariable Integer id,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cout", required = false) Long cout,
            @RequestParam(value = "idLaboratoire", required = false) Long idLaboratoire,
            @RequestParam(value = "epreuves", required = false) String epreuvesJson // JSON des Epreuves avec TestAnalyses
    ) {
        try {
            // Vérifier si l'Analyse existe
            Optional<Analyse> analyseOptional = analyseService.getAnalyseById(id);
            if (!analyseOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Analyse analyse = analyseOptional.get();

            // Mettre à jour les champs d'Analyse
            if (nom != null && !nom.isEmpty()) {
                analyse.setNom(nom);
            }
            if (description != null && !description.isEmpty()) {
                analyse.setDescription(description);
            }
            if (cout != null) {
                analyse.setCout(cout);
            }
            if (idLaboratoire != null) {
                analyse.setFkIdLaboratoire(idLaboratoire);
            }
            // Sauvegarder les modifications d'Analyse
            Analyse updatedAnalyse = analyseService.saveAnalyse(analyse);

            // Traiter les Epreuves et TestAnalyses
            if (epreuvesJson != null && !epreuvesJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<EpreuveRequest> epreuves = objectMapper.readValue(epreuvesJson, new TypeReference<List<EpreuveRequest>>() {});

                for (EpreuveRequest epreuve : epreuves) {
                    if (epreuve.getId() != null) {
                        // Mettre à jour une Epreuve existante
                        ResponseEntity<EpreuveRequest> epreuveResponse = restTemplate.exchange(
                                EpreuveServiceUrl+"/"+ epreuve.getId(),
                                HttpMethod.GET,
                                null,
                                EpreuveRequest.class
                        );

                        EpreuveRequest existingEpreuve = epreuveResponse.getBody();
                        if (existingEpreuve != null) {
                            existingEpreuve.setNom(epreuve.getNom());
                            existingEpreuve.setArchive(updatedAnalyse.isArchive());
                            restTemplate.put(EpreuveServiceUrl+"/"+ existingEpreuve.getId(), existingEpreuve);
                        } else {
                            // Créer une nouvelle Epreuve
                            epreuve.setFkIdAnalyse(updatedAnalyse.getId());
                            restTemplate.postForEntity(EpreuveServiceUrl+"/create", epreuve, EpreuveRequest.class);
                        }
                    } else {
                        // Créer une nouvelle Epreuve
                        epreuve.setFkIdAnalyse(updatedAnalyse.getId());
                        restTemplate.postForEntity(EpreuveServiceUrl+"/create", epreuve, EpreuveRequest.class);
                    }

                    // Traiter les TestAnalyses associés
                    List<TestAnalyseRequest> testAnalyses = epreuve.getTestAnalyses();
                    if (testAnalyses != null) {
                        System.out.println(testAnalyses);
                        for (TestAnalyseRequest testAnalyse : testAnalyses) {
                            if (testAnalyse.getId() != null) {
                                // Mettre à jour un TestAnalyse existant
                                ResponseEntity<TestAnalyseRequest> testAnalyseResponse = restTemplate.exchange(
                                        testAnalyseServiceUrl+"/"+ testAnalyse.getId(),
                                        HttpMethod.GET,
                                        null,
                                        TestAnalyseRequest.class
                                );

                                TestAnalyseRequest existingTestAnalyse = testAnalyseResponse.getBody();
                                System.out.println(existingTestAnalyse.getId());
                                if (existingTestAnalyse != null) {
                                    existingTestAnalyse.setNom(testAnalyse.getNom());
                                    existingTestAnalyse.setDescription(testAnalyse.getDescription());
                                    existingTestAnalyse.setValeursDeReference(testAnalyse.getValeursDeReference());
                                    existingTestAnalyse.setArchive(epreuve.isArchive());
                                    restTemplate.put(testAnalyseServiceUrl+"/"+ existingTestAnalyse.getId(), existingTestAnalyse);
                                } else {
                                    // Créer un nouveau TestAnalyse
                                    testAnalyse.setIdEpreuve(epreuve.getId());
                                    restTemplate.postForEntity(testAnalyseServiceUrl+"/create", testAnalyse, TestAnalyseRequest.class);
                                }
                            } else {
                                // Créer un nouveau TestAnalyse
                                testAnalyse.setIdEpreuve(epreuve.getId());
                                restTemplate.postForEntity(testAnalyseServiceUrl+"/create", testAnalyse, TestAnalyseRequest.class);
                            }
                        }
                    }
                }
            }

            return ResponseEntity.ok(updatedAnalyse);

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'Analyse : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PutMapping("/archive/{id}")
    public ResponseEntity<String> archiveAnalyse(@PathVariable Integer id) {
        try {
            // Étape 1 : Vérifier si l'analyse existe
            Optional<Analyse> analyseOptional = analyseService.getAnalyseById(id);
            if (!analyseOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Analyse non trouvée");
            }

            // Étape 2 : Archiver l'analyse
            Analyse analyse = analyseOptional.get();
            analyse.setArchive(true);
            analyseService.saveAnalyse(analyse);


            String epreuvesarchiveUrl = EpreuveServiceUrl+"/archive/by-analyse/" + id;
            ResponseEntity<String> epreuvearchiveResponse = restTemplate.exchange(
                    epreuvesarchiveUrl,
                    HttpMethod.PUT,
                    null,
                    String.class
            );

            if (!epreuvearchiveResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur lors de l'archivage des épreuves");
            }

// Étape 3 : Récupérer les épreuves associées via le microservice
            String epreuvesUrl = EpreuveServiceUrl + "/analyse/" + id; // Endpoint pour récupérer les épreuves
            ResponseEntity<List<EpreuveRequest>> epreuveResponse = restTemplate.exchange(
                    epreuvesUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EpreuveRequest>>() {}
            );

            if (!epreuveResponse.getStatusCode().is2xxSuccessful() || epreuveResponse.getBody() == null) {
                throw new RuntimeException("Erreur lors de la récupération des épreuves associées");
            }
            List<EpreuveRequest> epreuves = epreuveResponse.getBody(); // Liste des épreuves associées

            // Étape 4 : Archiver chaque épreuve et ses tests analyses
            for (EpreuveRequest epreuve : epreuves) {
                // Récupérer les tests analyses associés à l'épreuve
                String testsUrl = testAnalyseServiceUrl+"/archive/by-epreuve/"+ epreuve.getId();
                ResponseEntity<String> testsResponse = restTemplate.exchange(
                        testsUrl,
                        HttpMethod.PUT,
                        null,
                        String.class
                );

                if (!testsResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Erreur lors de l'archivage des tests analyses");
                }

            }

            return ResponseEntity.ok("Analyse, épreuves associées et tests analyses archivés avec succès");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'archivage : " + e.getMessage());
        }
    }




}



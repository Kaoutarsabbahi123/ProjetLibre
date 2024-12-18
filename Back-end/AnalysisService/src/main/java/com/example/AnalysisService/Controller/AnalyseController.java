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




}



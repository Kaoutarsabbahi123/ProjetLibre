package com.example.MicroTestService.Controller;

import com.example.MicroTestService.Entity.TestAnalyse;
import com.example.MicroTestService.Service.TestAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/testanalyses")
public class TestAnalyseController {
    @Autowired
    private TestAnalyseService testAnalyseService;

    @PostMapping("/create")
    public ResponseEntity<TestAnalyse> createTestAnalyse(@RequestBody TestAnalyse testAnalyse) {
        TestAnalyse savedTestAnalyse = testAnalyseService.saveTestAnalyse(testAnalyse);
        return new ResponseEntity<>(savedTestAnalyse, HttpStatus.CREATED);
    }
    @GetMapping("/epreuve/{idEpreuve}")
    public ResponseEntity<List<TestAnalyse>> getTestsByIdEpreuve(@PathVariable Integer idEpreuve) {
        List<TestAnalyse> tests = testAnalyseService.getTestsByIdEpreuve(idEpreuve);
        return ResponseEntity.ok(tests);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TestAnalyse> getTestAnalyseById(@PathVariable Integer id) {
        Optional<TestAnalyse> testAnalyseOptional = testAnalyseService.getTestsById(id);

        if (testAnalyseOptional.isPresent()) {
            return ResponseEntity.ok(testAnalyseOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestAnalyse> updateTestanalyse(@PathVariable Integer id, @RequestBody TestAnalyse testAnalyse) {
        // Mise à jour de l'adresse
        testAnalyse.setId(id); // S'assurer que l'adresse a le bon ID pour la mise à jour
        TestAnalyse updatedAddress = testAnalyseService.updateTestAnalyse(testAnalyse);

        return ResponseEntity.ok(updatedAddress); // Retourner l'adresse mise à jour

    }

    @PutMapping("/archive/by-epreuve/{idEpreuve}")
    public ResponseEntity<String> archiveTestsByEpreuve(@PathVariable Integer idEpreuve) {
        try {
            List<TestAnalyse> testsAnalyses = testAnalyseService.getTestsByIdEpreuve(idEpreuve);
            for (TestAnalyse testAnalyse : testsAnalyses) {
                testAnalyse.setArchive(true); // Archiver le test analyse
                testAnalyseService.saveTestAnalyse(testAnalyse);
            }

            return ResponseEntity.ok("Tests analyses associés à l'épreuve archivés avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'archivage des tests analyses");
        }
    }
}

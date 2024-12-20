package com.example.TestService.Controller;

import com.example.TestService.Entity.Epreuve;
import com.example.TestService.Service.EpreuveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/epreuves")
public class EpreuveController {
    @Autowired
    private EpreuveService epreuveService;
    @PostMapping("/create")
    public ResponseEntity<Epreuve> createTestAnalyse(@RequestBody Epreuve epreuve) {
        Epreuve savedEpreuve = epreuveService.saveEpreuve(epreuve);
        return new ResponseEntity<>(savedEpreuve, HttpStatus.CREATED);
    }
    @GetMapping("/analyse/{fkIdAnalyse}")
    public List<Epreuve> getEpreuvesByFkIdAnalyse(@PathVariable Integer fkIdAnalyse) {
        return epreuveService.getEpreuvesByFkIdAnalyse(fkIdAnalyse);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Epreuve> getEpreuveById(@PathVariable Integer id) {
        Optional<Epreuve> epreuveOptional = epreuveService.getEpreuveById(id);

        if (epreuveOptional.isPresent()) {
            return ResponseEntity.ok(epreuveOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Epreuve> updateAddress(@PathVariable Integer id, @RequestBody Epreuve epreuve) {
            // Mise à jour de l'adresse
            epreuve.setId(id); // S'assurer que l'adresse a le bon ID pour la mise à jour
            Epreuve updatedAddress = epreuveService.updateEpreuve(epreuve);

            return ResponseEntity.ok(updatedAddress); // Retourner l'adresse mise à jour

    }
    @PutMapping("/archive/by-analyse/{fkIdAnalyse}")
    public ResponseEntity<String> archiveEpreuvesByAnalyse(@PathVariable Integer fkIdAnalyse) {
        try {
            List<Epreuve> epreuves = epreuveService.getEpreuvesByFkIdAnalyse(fkIdAnalyse);
            for (Epreuve epreuve : epreuves) {
                epreuve.setArchive(true);
                epreuveService.saveEpreuve(epreuve);
            }

            return ResponseEntity.ok("Toutes les épreuves associées à l'analyse ont été archivées");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'archivage des épreuves associées");
        }
    }
}

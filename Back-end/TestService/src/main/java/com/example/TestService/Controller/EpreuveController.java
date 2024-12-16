package com.example.TestService.Controller;

import com.example.TestService.Entity.Epreuve;
import com.example.TestService.Service.EpreuveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

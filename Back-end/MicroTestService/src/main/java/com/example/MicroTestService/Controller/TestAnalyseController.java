package com.example.MicroTestService.Controller;

import com.example.MicroTestService.Entity.TestAnalyse;
import com.example.MicroTestService.Service.TestAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}

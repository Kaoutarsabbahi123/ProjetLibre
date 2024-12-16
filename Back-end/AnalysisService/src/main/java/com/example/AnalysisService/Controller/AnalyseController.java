package com.example.AnalysisService.Controller;

import com.example.AnalysisService.Service.AnalyseService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/analyses")
public class AnalyseController {

    private final AnalyseService analyseService;
    private final RestTemplate restTemplate;

    public AnalyseController(AnalyseService analyseService, RestTemplate restTemplate) {
        this.analyseService = analyseService;
        this.restTemplate = restTemplate;
    }
}

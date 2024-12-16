package com.example.TestService.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();  // Retourne un nouvel objet RestTemplate
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));  // Remplacez '*' par l'origine du frontend
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);  // Permet d'inclure les informations d'authentification (cookies, etc.)
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}


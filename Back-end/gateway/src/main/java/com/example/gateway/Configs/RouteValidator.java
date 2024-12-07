package com.example.gateway.Configs;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Liste des endpoints publics (non sécurisés)
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/validate"
    );

    // Predicate pour vérifier si l'URI est sécurisée ou non
    public Predicate<ServerHttpRequest> isSecured =
            request -> {
                System.out.println("Entering RouteValidator...");

                // Affiche l'URI de la requête
                System.out.println("Requested URI: " + request.getURI().getPath());

                // Affiche la liste des endpoints publics
                System.out.println("Open API Endpoints: " + openApiEndpoints);

                // Vérifie si l'URI correspond à l'un des endpoints publics
                boolean isSecured = openApiEndpoints.stream()
                        .noneMatch(uri -> {
                            System.out.println("Checking URI: " + uri);
                            boolean matches = request.getURI().getPath().contains(uri);
                            System.out.println("URI Match Found: " + matches);
                            return matches;
                        });

                // Affiche le statut final de la sécurité
                System.out.println("Final Secured Status: " + isSecured);
                return isSecured;
            };
}

package com.example.AuthService;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // Configuration et initialisation du WebDriver
       // WebDriverManager.chromedriver()
         //       .driverVersion("131.0") // Remplacez par une version compatible de ChromeDriver
           //     .setup();
        //driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Exécuter en mode sans tête
        options.addArguments("--no-sandbox"); // Nécessaire dans CI/CD
        options.addArguments("--disable-dev-shm-usage"); // Éviter les problèmes de mémoire partagée
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        // Fermer le navigateur après chaque test
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testLoginPage() {
        // Naviguer vers la page de connexion
        driver.get("http://localhost:4200/login");

        // Attendre que les champs soient visibles
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

        // Saisir des informations de connexion
        usernameField.sendKeys("user123@gmail.com");
        passwordField.sendKeys("123");

        // Soumettre le formulaire
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.submit();

        // Vérifier que la page après connexion est correcte
        wait.until(ExpectedConditions.titleIs("SmartLabo")); // Remplacez "Home" par le titre attendu
        assertTrue(driver.getTitle().contains("SmartLabo"), "Le titre de la page ne contient pas 'SmartLabo'.");
    }
}
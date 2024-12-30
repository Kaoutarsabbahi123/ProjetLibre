package com.example.LaboratoryService.ControllerTest;

import com.example.LaboratoryService.DTO.AdressRequest;
import com.example.LaboratoryService.DTO.ContactRequest;
import com.example.LaboratoryService.Entity.Laboratoire;
import com.example.LaboratoryService.Service.LaboratoireService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LaboratoireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LaboratoireService laboratoireService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testCreateLaboratoire_NomObligatoire() throws Exception {
        mockMvc.perform(post("/api/laboratoires/createlabo")
                        .param("nrc", "12345")
                        .param("dateActivation", "2024-12-29"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateLaboratoire_LogoNonFourni() throws Exception {
        when(laboratoireService.saveLaboratoire(any())).thenReturn(new Laboratoire());

        mockMvc.perform(post("/api/laboratoires/createlabo")
                        .param("nom", "Laboratoire Test")
                        .param("nrc", "12345")
                        .param("actif", "true")
                        .param("dateActivation", "2024-12-29") // Format de date correct
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

    }

    @Test
    public void testCreateLaboratoire_LogoFourni() throws Exception {
        MockMultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", "test".getBytes());
        when(laboratoireService.saveLaboratoire(any())).thenReturn(new Laboratoire());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/laboratoires/createlabo")
                        .file(logo)
                        .param("nom", "Laboratoire Test")
                        .param("nrc", "12345")
                        .param("dateActivation", "2024-12-29"))
                .andExpect(status().isOk());
    }


    @Test
    public void testCreateLaboratoire_ContactsJsonInvalide() throws Exception {
        mockMvc.perform(post("/api/laboratoires/createlabo")
                        .param("nom", "Laboratoire Test")
                        .param("nrc", "12345")
                        .param("contacts", "INVALID_JSON"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateLaboratoire_ContactsJsonValide() throws Exception {
        String contactsJson = "[{\"numTel\":\"123456789\",\"adresses\":[{\"numVoie\":\"123\",\"nomVoie\":\"Rue Test\",\"codePostal\":\"12345\",\"ville\":\"Ville Test\",\"commune\":\"Commune Test\"}]}]";

        Laboratoire savedLaboratoire = new Laboratoire();
        savedLaboratoire.setId(1L);

        when(laboratoireService.saveLaboratoire(any())).thenReturn(savedLaboratoire);
        when(restTemplate.postForEntity(anyString(), any(), eq(ContactRequest.class)))
                .thenReturn(ResponseEntity.ok(new ContactRequest(
                        1L,
                        "123456789",
                        "987654321",
                        "contact@example.com",
                        1L,
                        null
                )));
        when(restTemplate.postForEntity(anyString(), any(), eq(AdressRequest.class)))
                .thenReturn(ResponseEntity.ok(new AdressRequest()));

        mockMvc.perform(post("/api/laboratoires/createlabo")
                        .param("nom", "Laboratoire Test")
                        .param("nrc", "12345")
                        .param("actif", "true")
                        .param("dateActivation", "2024-12-29")
                        .param("contacts", contactsJson))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetAllLaboratoires() throws Exception {
        List<Laboratoire> laboratoires = Arrays.asList(
                new Laboratoire(1L, "Labo 1", "logo1.png".getBytes(), "NRC001", true, LocalDate.of(2024, 12, 29)),
                new Laboratoire(2L, "Labo 2", "logo2.png".getBytes(), "NRC002", true, LocalDate.of(2024, 12, 30))
        );

        when(laboratoireService.getAllLaboratoires()).thenReturn(laboratoires);

        mockMvc.perform(get("/api/laboratoires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(laboratoires.size()))
                .andExpect(jsonPath("$[0].nom").value("Labo 1"))
                .andExpect(jsonPath("$[1].nom").value("Labo 2"));
    }

}



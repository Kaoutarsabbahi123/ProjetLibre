package com.example.LaboratoryService.ServiceTest;

import com.example.LaboratoryService.Entity.Laboratoire;
import com.example.LaboratoryService.Repository.LaboratoireRepository;
import com.example.LaboratoryService.Service.LaboratoireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LaboratoireServiceTest {

    @Mock
    private LaboratoireRepository laboratoireRepository;

    @InjectMocks
    private LaboratoireService laboratoireService;

    private Laboratoire laboratoire;

    @BeforeEach
    void setUp() {
        // Initialisation avec des données d'exemple pour la classe Laboratoire
        laboratoire = new Laboratoire(
                1L,
                "Laboratoire A",
                new byte[]{1, 2, 3},  // Exemple de logo (en byte[])
                "NRC123",
                true,
                LocalDate.now()
        );
    }

    @Test
    void testSaveLaboratoire() {
        // Arrange
        when(laboratoireRepository.save(any(Laboratoire.class))).thenReturn(laboratoire);

        // Act
        Laboratoire savedLaboratoire = laboratoireService.saveLaboratoire(laboratoire);

        // Assert
        assertNotNull(savedLaboratoire);
        assertEquals(laboratoire.getNom(), savedLaboratoire.getNom());
        assertEquals(laboratoire.getNrc(), savedLaboratoire.getNrc());
        assertTrue(savedLaboratoire.isActive());
        verify(laboratoireRepository, times(1)).save(laboratoire);
    }

    @Test
    void testGetLaboratoireById() {
        // Arrange
        when(laboratoireRepository.findById(1L)).thenReturn(Optional.of(laboratoire));

        // Act
        Optional<Laboratoire> foundLaboratoire = laboratoireService.getLaboratoireById(1L);

        // Assert
        assertTrue(foundLaboratoire.isPresent());
        assertEquals(laboratoire.getNom(), foundLaboratoire.get().getNom());
        assertEquals(laboratoire.getNrc(), foundLaboratoire.get().getNrc());
        verify(laboratoireRepository, times(1)).findById(1L);
    }

    @Test
    void testGetLaboratoireByIdNotFound() {
        // Arrange
        when(laboratoireRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Laboratoire> foundLaboratoire = laboratoireService.getLaboratoireById(1L);

        // Assert
        assertFalse(foundLaboratoire.isPresent());
        verify(laboratoireRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllLaboratoires() {
        // Arrange
        List<Laboratoire> laboratoires = Arrays.asList(laboratoire, new Laboratoire(2L, "Laboratoire B", new byte[]{4, 5, 6}, "NRC456", false, LocalDate.now().minusDays(1)));
        when(laboratoireRepository.findAll()).thenReturn(laboratoires);

        // Act
        List<Laboratoire> result = laboratoireService.getAllLaboratoires();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laboratoire A", result.get(0).getNom());
        assertEquals("Laboratoire B", result.get(1).getNom());
        verify(laboratoireRepository, times(1)).findAll();
    }

    @Test
    void testSearchLaboratoiresByKeyword() {
        // Arrange
        List<Laboratoire> laboratoires = Arrays.asList(laboratoire, new Laboratoire(2L, "Laboratoire B", new byte[]{4, 5, 6}, "NRC456", false, LocalDate.now().minusDays(1)));
        when(laboratoireRepository.searchLaboratoiresByKeyword("Laboratoire")).thenReturn(laboratoires);

        // Act
        List<Laboratoire> result = laboratoireService.searchLaboratoiresByKeyword("Laboratoire");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(laboratoireRepository, times(1)).searchLaboratoiresByKeyword("Laboratoire");
    }

    @Test
    void testGetNomLaboratoireById() {
        // Arrange
        when(laboratoireRepository.findById(1L)).thenReturn(Optional.of(laboratoire));

        // Act
        String nom = laboratoireService.getNomLaboratoireById(1L);

        // Assert
        assertEquals(laboratoire.getNom(), nom);
        verify(laboratoireRepository, times(1)).findById(1L);
    }

    @Test
    void testGetNomLaboratoireByIdNotFound() {
        // Arrange
        when(laboratoireRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            laboratoireService.getNomLaboratoireById(1L);
        });
        assertEquals("Laboratoire non trouvé avec l'id : 1", thrown.getMessage());
        verify(laboratoireRepository, times(1)).findById(1L);
    }
}


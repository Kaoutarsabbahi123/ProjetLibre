package com.example.FolderService.Controllers;

import com.example.FolderService.Entities.Folder;
import com.example.FolderService.Services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping
    public List<Folder> getAllDossiers() {
        return folderService.getAllDossiers();
    }
    @PostMapping("/create")
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder) {
        Folder savedFolder = folderService.createFolder(folder);
        return ResponseEntity.ok(savedFolder);
    }
    @PutMapping("/update/{numDossier}")
    public ResponseEntity<Folder> updateFolder(
            @PathVariable Integer numDossier,
            @RequestBody Folder folder) {
        Folder updatedFolder = folderService.updateFolder(numDossier, folder);
        return ResponseEntity.ok(updatedFolder);
    }
    @PatchMapping("/archive/{numDossier}")
    public ResponseEntity<String> archiveFolder(@PathVariable Integer numDossier) {
        try {
            folderService.archiveFolder(numDossier);
            return ResponseEntity.ok("Folder archivé avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
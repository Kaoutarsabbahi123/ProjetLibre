package com.example.FolderService.Controllers;

import com.example.FolderService.Entities.Folder;
import com.example.FolderService.Services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
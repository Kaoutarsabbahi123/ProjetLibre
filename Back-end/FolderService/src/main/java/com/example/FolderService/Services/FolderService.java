package com.example.FolderService.Services;

import com.example.FolderService.Entities.Folder;
import com.example.FolderService.Repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    public List<Folder> getAllDossiers() {
        return folderRepository.findAll();
    }
}

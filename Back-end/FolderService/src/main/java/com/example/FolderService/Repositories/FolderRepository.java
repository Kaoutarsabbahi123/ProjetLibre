package com.example.FolderService.Repositories;


import com.example.FolderService.Entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    @Override
    Optional<Folder> findById(Integer integer);
}


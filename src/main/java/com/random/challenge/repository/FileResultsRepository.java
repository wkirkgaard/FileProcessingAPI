package com.random.challenge.repository;

import com.random.challenge.entities.FileResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository

public interface FileResultsRepository extends JpaRepository<FileResultsEntity, String> {

    public Optional<FileResultsEntity> findByFilename(String filename);

    public Optional<FileResultsEntity> getFileResultsEntityById(int id);
}

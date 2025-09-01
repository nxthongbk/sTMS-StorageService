package com.scity.storage.repository;

import com.scity.storage.model.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

  Optional<File> findById(String id);
}

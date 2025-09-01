package com.scity.storage.repository;

import com.scity.storage.model.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> {
    @Query(value = "SELECT * FROM app_info ORDER BY updated_at DESC LIMIT 1", nativeQuery = true)
    Version findLatestUpdated();
}

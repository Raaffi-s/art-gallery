package com.gallery.catalog.repository;

import com.gallery.catalog.model.Gallery;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    @EntityGraph(attributePaths = {"owner", "paintings"})
    List<Gallery> findAll();

    @EntityGraph(attributePaths = {"owner", "paintings"})
    Optional<Gallery> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"owner", "paintings"})
    List<Gallery> findByOwnerId(Long ownerId);

    Optional<Gallery> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COALESCE(MAX(g.demoNumber), 0) FROM Gallery g")
    Long findMaxDemoNumberForUpdate();

    @Query("SELECT g FROM Gallery g WHERE g.demoNumber = :demoNumber")
    Optional<Gallery> findByDemoNumber(@Param("demoNumber") Long demoNumber);
}
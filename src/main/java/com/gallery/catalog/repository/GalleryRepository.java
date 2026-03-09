package com.gallery.catalog.repository;

import com.gallery.catalog.model.Gallery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    List<Gallery> findByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"owner", "paintings"})
    Optional<Gallery> findWithDetailsById(Long id);
}
package com.gallery.catalog.repository;

import com.gallery.catalog.model.Painting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    @EntityGraph(attributePaths = {"gallery", "tags"})
    List<Painting> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"gallery", "tags"})
    Optional<Painting> findWithDetailsById(Long id);

    List<Painting> findByArtistContainingIgnoreCase(String artist);
}
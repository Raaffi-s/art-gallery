package com.gallery.catalog.repository;

import com.gallery.catalog.model.Painting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    List<Painting> findByArtistContainingIgnoreCase(String artist);

    List<Painting> findByTitleContainingIgnoreCase(String title);

    List<Painting> findByYearBetween(Integer startYear, Integer endYear);

    @EntityGraph(attributePaths = {"user", "gallery", "tags"})
    List<Painting> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT p FROM Painting p "
        + "LEFT JOIN FETCH p.user "
        + "LEFT JOIN FETCH p.gallery "
        + "LEFT JOIN FETCH p.tags "
        + "WHERE LOWER(p.artist) LIKE LOWER(CONCAT('%', :artist, '%'))")
    List<Painting> findByArtistWithDetails(@Param("artist") String artist);

    @EntityGraph(attributePaths = {"user", "gallery", "tags"})
    Optional<Painting> findWithDetailsById(Long id);
}
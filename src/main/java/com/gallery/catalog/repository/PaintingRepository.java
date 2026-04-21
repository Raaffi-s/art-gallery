package com.gallery.catalog.repository;

import com.gallery.catalog.model.Painting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    List<Painting> findByArtistContainingIgnoreCase(String artist);

    @EntityGraph(attributePaths = {"gallery", "tags"})
    List<Painting> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT DISTINCT p
        FROM Painting p
        LEFT JOIN FETCH p.gallery
        LEFT JOIN FETCH p.tags
        WHERE LOWER(p.artist) LIKE LOWER(CONCAT('%', :artist, '%'))
        """)
    List<Painting> findByArtistWithDetails(@Param("artist") String artist);

    @EntityGraph(attributePaths = {"gallery", "tags"})
    Optional<Painting> findWithDetailsById(Long id);

    // Пункт 1 — JPQL фильтрация по вложенной сущности
    @Query("""
        SELECT DISTINCT p
        FROM Painting p
        LEFT JOIN FETCH p.gallery g
        LEFT JOIN FETCH p.tags
        WHERE LOWER(g.name) = LOWER(:galleryName)
        """)
    List<Painting> findByGalleryName(@Param("galleryName") String galleryName);

    // Пункт 2 — Native query аналог
    @Query(
        value = """
            SELECT DISTINCT p.*
            FROM paintings p
            JOIN galleries g ON p.gallery_id = g.id
            WHERE LOWER(g.name) = LOWER(:galleryName)
            """,
        nativeQuery = true
    )
    List<Painting> findByGalleryNameNative(@Param("galleryName") String galleryName);

    // Пункт 3 — пагинация
    @Query(
        value = "SELECT p FROM Painting p LEFT JOIN p.gallery g WHERE LOWER(g.name) = LOWER(:galleryName)",
        countQuery = "SELECT COUNT(p) FROM Painting p LEFT JOIN p.gallery g WHERE LOWER(g.name) = LOWER(:galleryName)"
    )
    Page<Painting> findByGalleryNamePaged(@Param("galleryName") String galleryName, Pageable pageable);
}
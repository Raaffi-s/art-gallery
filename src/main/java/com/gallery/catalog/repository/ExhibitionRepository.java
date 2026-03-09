package com.gallery.catalog.repository;

import com.gallery.catalog.model.Exhibition;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @EntityGraph(attributePaths = {"paintings"})
    List<Exhibition> findAllByOrderByStartDateDesc();
}
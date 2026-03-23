package com.gallery.catalog.controller;

import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.service.ExhibitionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    public ExhibitionController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @GetMapping
    public ResponseEntity<List<ExhibitionDto>> getAllExhibitions() {
        return ResponseEntity.ok(exhibitionService.getAllExhibitions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionDto> getExhibitionById(@PathVariable Long id) {
        return ResponseEntity.ok(exhibitionService.getExhibitionById(id));
    }

    @PostMapping
    public ResponseEntity<ExhibitionDto> createExhibition(@RequestBody ExhibitionDto dto) {
        ExhibitionDto created = exhibitionService.createExhibition(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExhibitionDto> updateExhibition(
        @PathVariable Long id,
        @RequestBody ExhibitionDto dto) {
        return ResponseEntity.ok(exhibitionService.updateExhibition(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExhibition(@PathVariable Long id) {
        exhibitionService.deleteExhibition(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{exhibitionId}/paintings/{paintingId}")
    public ResponseEntity<ExhibitionDto> addPainting(
        @PathVariable Long exhibitionId,
        @PathVariable Long paintingId) {
        return ResponseEntity.ok(
            exhibitionService.addPaintingToExhibition(exhibitionId, paintingId));
    }

    @DeleteMapping("/{exhibitionId}/paintings/{paintingId}")
    public ResponseEntity<ExhibitionDto> removePainting(
        @PathVariable Long exhibitionId,
        @PathVariable Long paintingId) {
        return ResponseEntity.ok(
            exhibitionService.removePaintingFromExhibition(exhibitionId, paintingId));
    }
}

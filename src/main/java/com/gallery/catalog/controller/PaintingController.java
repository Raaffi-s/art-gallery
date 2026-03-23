package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.service.PaintingService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paintings")
public class PaintingController {

    private final PaintingService paintingService;

    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public ResponseEntity<List<PaintingDto>> getPaintings(
        @RequestParam(required = false) String artist) {
        if (artist != null && !artist.isEmpty()) {
            return ResponseEntity.ok(paintingService.getPaintingsByArtist(artist));
        }
        return ResponseEntity.ok(paintingService.getAllPaintings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaintingDto> getPaintingById(@PathVariable Long id) {
        return ResponseEntity.ok(paintingService.getPaintingById(id));
    }

    @PostMapping
    public ResponseEntity<PaintingDto> createPainting(@RequestBody PaintingDto dto) {
        PaintingDto created = paintingService.createPainting(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaintingDto> updatePainting(
        @PathVariable Long id,
        @RequestBody PaintingDto dto) {
        return ResponseEntity.ok(paintingService.updatePainting(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePainting(@PathVariable Long id) {
        paintingService.deletePainting(id);
        return ResponseEntity.noContent().build();
    }
}

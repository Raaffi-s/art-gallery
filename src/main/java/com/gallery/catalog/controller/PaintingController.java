package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.service.PaintingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        @RequestParam(required = false) String artist
    ) {
        if (artist != null && !artist.isBlank()) {
            return ResponseEntity.ok(paintingService.getPaintingsByArtist(artist));
        }
        return ResponseEntity.ok(paintingService.getAllPaintings());
    }

    @GetMapping("/galleries")
    public ResponseEntity<List<PaintingDto>> getPaintingsByGalleryJpql(
        @RequestParam String galleryName
    ) {
        return ResponseEntity.ok(paintingService.getPaintingsByGalleryName(galleryName));
    }

    @GetMapping("/galleries/native")
    public ResponseEntity<List<PaintingDto>> getPaintingsByGalleryNative(
        @RequestParam String galleryName
    ) {
        return ResponseEntity.ok(paintingService.getPaintingsByGalleryNameNative(galleryName));
    }

    @GetMapping("/galleries/paged")
    public ResponseEntity<Page<PaintingDto>> getPaintingsByGalleryPaged(
        @RequestParam String galleryName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
            paintingService.getPaintingsByGalleryNamePaged(galleryName, page, size)
        );
    }

    @GetMapping("/galleries/cached")
    public ResponseEntity<List<PaintingDto>> getPaintingsByGalleryCached(
        @RequestParam String galleryName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
            paintingService.getPaintingsByGalleryNameCached(galleryName, page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaintingDto> getPaintingById(@PathVariable Long id) {
        return ResponseEntity.ok(paintingService.getPaintingById(id));
    }

    @PostMapping
    public ResponseEntity<PaintingDto> createPainting(@Valid @RequestBody PaintingDto dto) {
        PaintingDto created = paintingService.createPainting(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaintingDto> updatePainting(
        @PathVariable Long id,
        @Valid @RequestBody PaintingDto dto
    ) {
        return ResponseEntity.ok(paintingService.updatePainting(id, dto));
    }

    @PatchMapping("/{id}/tags/{tagName}")
    public ResponseEntity<PaintingDto> addTagToPainting(
        @PathVariable Long id,
        @PathVariable String tagName
    ) {
        return ResponseEntity.ok(paintingService.addTagToPainting(id, tagName));
    }

    @DeleteMapping("/{id}/tags/{tagName}")
    public ResponseEntity<PaintingDto> removeTagFromPainting(
        @PathVariable Long id,
        @PathVariable String tagName
    ) {
        return ResponseEntity.ok(paintingService.removeTagFromPainting(id, tagName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePainting(@PathVariable Long id) {
        paintingService.deletePainting(id);
        return ResponseEntity.noContent().build();
    }
}
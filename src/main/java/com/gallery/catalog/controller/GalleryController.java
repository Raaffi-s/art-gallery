package com.gallery.catalog.controller;

import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.service.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/galleries")
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @Operation(summary = "Получить список галерей (опционально по ownerId)")
    @ApiResponse(responseCode = "200", description = "Галереи успешно получены")
    @GetMapping
    public ResponseEntity<List<GalleryDto>> getAllGalleries(
        @RequestParam(required = false) Long ownerId
    ) {
        if (ownerId != null) {
            return ResponseEntity.ok(galleryService.getGalleriesByOwner(ownerId));
        }
        return ResponseEntity.ok(galleryService.getAllGalleries());
    }

    @Operation(summary = "Получить галерею по ID")
    @ApiResponse(responseCode = "200", description = "Галерея найдена")
    @ApiResponse(responseCode = "404", description = "Галерея не найдена")
    @GetMapping("/{id}")
    public ResponseEntity<GalleryDto> getGalleryById(@PathVariable Long id) {
        return ResponseEntity.ok(galleryService.getGalleryById(id));
    }

    @Operation(summary = "Создать новую галерею")
    @ApiResponse(responseCode = "201", description = "Галерея успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping
    public ResponseEntity<GalleryDto> createGallery(@RequestBody GalleryDto dto) {
        GalleryDto created = galleryService.createGallery(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить галерею по ID")
    @ApiResponse(responseCode = "200", description = "Галерея успешно обновлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @ApiResponse(responseCode = "404", description = "Галерея не найдена")
    @PutMapping("/{id}")
    public ResponseEntity<GalleryDto> updateGallery(
        @PathVariable Long id,
        @RequestBody GalleryDto dto
    ) {
        return ResponseEntity.ok(galleryService.updateGallery(id, dto));
    }

    @Operation(summary = "Удалить галерею по ID")
    @ApiResponse(responseCode = "204", description = "Галерея успешно удалена")
    @ApiResponse(responseCode = "404", description = "Галерея не найдена")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGallery(@PathVariable Long id) {
        galleryService.deleteGallery(id);
        return ResponseEntity.noContent().build();
    }
}
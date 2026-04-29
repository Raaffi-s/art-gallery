package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.service.PaintingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Получить список картин (опционально фильтр по художнику)")
    @ApiResponse(responseCode = "200", description = "Картины успешно получены")
    @GetMapping
    public ResponseEntity<List<PaintingDto>> getPaintings(
        @RequestParam(required = false) String artist
    ) {
        if (artist != null && !artist.isBlank()) {
            return ResponseEntity.ok(paintingService.getPaintingsByArtist(artist));
        }
        return ResponseEntity.ok(paintingService.getAllPaintings());
    }

    @Operation(summary = "Получить картины по названию галереи (JPQL)")
    @ApiResponse(responseCode = "200", description = "Картины успешно получены")
    @GetMapping("/galleries")
    public ResponseEntity<List<PaintingDto>> getPaintingsByGalleryJpql(
        @RequestParam String galleryName
    ) {
        return ResponseEntity.ok(paintingService.getPaintingsByGalleryName(galleryName));
    }

    @Operation(summary = "Получить картины по названию галереи (native query)")
    @ApiResponse(responseCode = "200", description = "Картины успешно получены")
    @GetMapping("/galleries/native")
    public ResponseEntity<List<PaintingDto>> getPaintingsByGalleryNative(
        @RequestParam String galleryName
    ) {
        return ResponseEntity.ok(paintingService.getPaintingsByGalleryNameNative(galleryName));
    }

    @Operation(summary = "Получить картины по галерее с пагинацией")
    @ApiResponse(responseCode = "200", description = "Картины успешно получены")
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

    @Operation(summary = "Получить картины по галерее с кэшированием")
    @ApiResponse(responseCode = "200", description = "Картины успешно получены")
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

    @Operation(summary = "Получить картину по ID")
    @ApiResponse(responseCode = "200", description = "Картина найдена")
    @ApiResponse(responseCode = "404", description = "Картина не найдена")
    @GetMapping("/{id}")
    public ResponseEntity<PaintingDto> getPaintingById(@PathVariable Long id) {
        return ResponseEntity.ok(paintingService.getPaintingById(id));
    }

    @Operation(summary = "Создать новую картину")
    @ApiResponse(responseCode = "201", description = "Картина успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping
    public ResponseEntity<PaintingDto> createPainting(@Valid @RequestBody PaintingDto dto) {
        PaintingDto created = paintingService.createPainting(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить существующую картину по ID")
    @ApiResponse(responseCode = "200", description = "Картина успешно обновлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @ApiResponse(responseCode = "404", description = "Картина не найдена")
    @PutMapping("/{id}")
    public ResponseEntity<PaintingDto> updatePainting(
        @PathVariable Long id,
        @Valid @RequestBody PaintingDto dto
    ) {
        return ResponseEntity.ok(paintingService.updatePainting(id, dto));
    }

    @Operation(summary = "Добавить тег к картине")
    @ApiResponse(responseCode = "200", description = "Тег успешно добавлен")
    @ApiResponse(responseCode = "404", description = "Картина не найдена")
    @PatchMapping("/{id}/tags/{tagName}")
    public ResponseEntity<PaintingDto> addTagToPainting(
        @PathVariable Long id,
        @PathVariable String tagName
    ) {
        return ResponseEntity.ok(paintingService.addTagToPainting(id, tagName));
    }

    @Operation(summary = "Удалить тег у картины")
    @ApiResponse(responseCode = "200", description = "Тег успешно удалён")
    @ApiResponse(responseCode = "404", description = "Картина не найдена")
    @DeleteMapping("/{id}/tags/{tagName}")
    public ResponseEntity<PaintingDto> removeTagFromPainting(
        @PathVariable Long id,
        @PathVariable String tagName
    ) {
        return ResponseEntity.ok(paintingService.removeTagFromPainting(id, tagName));
    }

    @Operation(summary = "Удалить картину по ID")
    @ApiResponse(responseCode = "204", description = "Картина успешно удалена")
    @ApiResponse(responseCode = "404", description = "Картина не найдена")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePainting(@PathVariable Long id) {
        paintingService.deletePainting(id);
        return ResponseEntity.noContent().build();
    }
}
package com.gallery.catalog.controller;

import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.service.ExhibitionService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    public ExhibitionController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @Operation(summary = "Получить список всех выставок")
    @ApiResponse(responseCode = "200", description = "Выставки успешно получены")
    @GetMapping
    public ResponseEntity<List<ExhibitionDto>> getAllExhibitions() {
        return ResponseEntity.ok(exhibitionService.getAllExhibitions());
    }

    @Operation(summary = "Получить выставку по ID")
    @ApiResponse(responseCode = "200", description = "Выставка найдена")
    @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionDto> getExhibitionById(@PathVariable Long id) {
        return ResponseEntity.ok(exhibitionService.getExhibitionById(id));
    }

    @Operation(summary = "Создать новую выставку")
    @ApiResponse(responseCode = "201", description = "Выставка успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping
    public ResponseEntity<ExhibitionDto> createExhibition(@RequestBody ExhibitionDto dto) {
        ExhibitionDto created = exhibitionService.createExhibition(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить выставку по ID")
    @ApiResponse(responseCode = "200", description = "Выставка успешно обновлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    @PutMapping("/{id}")
    public ResponseEntity<ExhibitionDto> updateExhibition(
        @PathVariable Long id,
        @RequestBody ExhibitionDto dto
    ) {
        return ResponseEntity.ok(exhibitionService.updateExhibition(id, dto));
    }

    @Operation(summary = "Удалить выставку по ID")
    @ApiResponse(responseCode = "204", description = "Выставка успешно удалена")
    @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExhibition(@PathVariable Long id) {
        exhibitionService.deleteExhibition(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить картину на выставку")
    @ApiResponse(responseCode = "200", description = "Картина успешно добавлена на выставку")
    @ApiResponse(responseCode = "404", description = "Выставка или картина не найдена")
    @PostMapping("/{exhibitionId}/paintings/{paintingId}")
    public ResponseEntity<ExhibitionDto> addPainting(
        @PathVariable Long exhibitionId,
        @PathVariable Long paintingId
    ) {
        return ResponseEntity.ok(
            exhibitionService.addPaintingToExhibition(exhibitionId, paintingId)
        );
    }

    @Operation(summary = "Удалить картину с выставки")
    @ApiResponse(responseCode = "200", description = "Картина успешно удалена с выставки")
    @ApiResponse(responseCode = "404", description = "Выставка или картина не найдена")
    @DeleteMapping("/{exhibitionId}/paintings/{paintingId}")
    public ResponseEntity<ExhibitionDto> removePainting(
        @PathVariable Long exhibitionId,
        @PathVariable Long paintingId
    ) {
        return ResponseEntity.ok(
            exhibitionService.removePaintingFromExhibition(exhibitionId, paintingId)
        );
    }
}
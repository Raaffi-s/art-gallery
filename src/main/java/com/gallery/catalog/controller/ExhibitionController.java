package com.gallery.catalog.controller;

import com.gallery.catalog.dto.BulkPaintingIdsDto;
import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.service.ExhibitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Exhibition Controller", description = "API для управления выставками и их картинами")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    public ExhibitionController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @Operation(summary = "Получить все выставки", description = "Возвращает список всех выставок")
    @ApiResponse(responseCode = "200", description = "Список выставок успешно получен")
    @GetMapping
    public ResponseEntity<List<ExhibitionDto>> getAllExhibitions() {
        return ResponseEntity.ok(exhibitionService.getAllExhibitions());
    }

    @Operation(summary = "Получить выставку по ID", description = "Возвращает одну выставку по её идентификатору")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Выставка найдена"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionDto> getExhibitionById(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(exhibitionService.getExhibitionById(id));
    }

    @Operation(summary = "Создать выставку", description = "Создаёт новую выставку")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Выставка успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<ExhibitionDto> createExhibition(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "DTO новой выставки",
            required = true,
            content = @Content(schema = @Schema(implementation = ExhibitionDto.class))
        )
        @RequestBody ExhibitionDto dto
    ) {
        ExhibitionDto created = exhibitionService.createExhibition(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить выставку", description = "Обновляет данные существующей выставки")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Выставка успешно обновлена"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ExhibitionDto> updateExhibition(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long id,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "DTO с обновлёнными данными выставки",
            required = true,
            content = @Content(schema = @Schema(implementation = ExhibitionDto.class))
        )
        @RequestBody ExhibitionDto dto
    ) {
        return ResponseEntity.ok(exhibitionService.updateExhibition(id, dto));
    }

    @Operation(summary = "Удалить выставку", description = "Удаляет выставку по ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Выставка успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExhibition(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long id
    ) {
        exhibitionService.deleteExhibition(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить одну картину в выставку", description = "Добавляет картину по её ID в выбранную выставку")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Картина успешно добавлена"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена"),
        @ApiResponse(responseCode = "400", description = "Картина не найдена")
    })
    @PostMapping("/{exhibitionId}/paintings/{paintingId}")
    public ResponseEntity<ExhibitionDto> addPainting(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long exhibitionId,

        @Parameter(description = "ID картины", example = "2")
        @PathVariable Long paintingId
    ) {
        return ResponseEntity.ok(
            exhibitionService.addPaintingToExhibition(exhibitionId, paintingId)
        );
    }

    @Operation(summary = "Удалить картину из выставки", description = "Удаляет картину по её ID из выбранной выставки")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Картина успешно удалена из выставки"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    })
    @DeleteMapping("/{exhibitionId}/paintings/{paintingId}")
    public ResponseEntity<ExhibitionDto> removePainting(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long exhibitionId,

        @Parameter(description = "ID картины", example = "2")
        @PathVariable Long paintingId
    ) {
        return ResponseEntity.ok(
            exhibitionService.removePaintingFromExhibition(exhibitionId, paintingId)
        );
    }

    @Operation(
        summary = "Массово добавить картины в выставку",
        description = "Bulk-операция добавления списка картин в выставку с использованием транзакции"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Картины успешно добавлены"),
        @ApiResponse(responseCode = "400", description = "Некорректный список ID или одна из картин не найдена"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    })
    @PostMapping("/{exhibitionId}/paintings/bulk")
    public ResponseEntity<ExhibitionDto> addPaintingsBulk(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long exhibitionId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "DTO со списком ID картин для массового добавления",
            required = true,
            content = @Content(schema = @Schema(implementation = BulkPaintingIdsDto.class))
        )
        @RequestBody BulkPaintingIdsDto dto
    ) {
        return ResponseEntity.ok(
            exhibitionService.addPaintingsToExhibitionBulk(exhibitionId, dto)
        );
    }

    @Operation(
        summary = "Массово добавить картины без транзакции",
        description = "Bulk-операция без @Transactional для демонстрации частичного сохранения данных при ошибке"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Картины успешно добавлены"),
        @ApiResponse(responseCode = "400", description = "Одна из картин не найдена, возможны частично сохранённые изменения"),
        @ApiResponse(responseCode = "404", description = "Выставка не найдена")
    })
    @PostMapping("/{exhibitionId}/paintings/bulk-no-tx")
    public ResponseEntity<ExhibitionDto> addPaintingsBulkWithoutTransaction(
        @Parameter(description = "ID выставки", example = "1")
        @PathVariable Long exhibitionId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "DTO со списком ID картин для массового добавления без транзакции",
            required = true,
            content = @Content(schema = @Schema(implementation = BulkPaintingIdsDto.class))
        )
        @RequestBody BulkPaintingIdsDto dto
    ) {
        return ResponseEntity.ok(
            exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(exhibitionId, dto)
        );
    }
}
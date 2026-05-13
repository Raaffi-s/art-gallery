package com.gallery.catalog.controller;

import com.gallery.catalog.dto.TaskInfoDto;
import com.gallery.catalog.exception.ErrorResponse;
import com.gallery.catalog.service.async.ExhibitionAsyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/async/exhibitions")
public class ExhibitionAsyncController {

    private final ExhibitionAsyncService exhibitionAsyncService;

    public ExhibitionAsyncController(ExhibitionAsyncService exhibitionAsyncService) {
        this.exhibitionAsyncService = exhibitionAsyncService;
    }

    @Operation(summary = "Запустить асинхронный анализ выставки")
    @ApiResponse(responseCode = "200", description = "Задача успешно создана")
    @ApiResponse(
        responseCode = "404",
        description = "Выставка не найдена",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/{exhibitionId}/analyze")
    public ResponseEntity<Map<String, UUID>> startAnalysis(@PathVariable Long exhibitionId) {
        UUID taskId = exhibitionAsyncService.startExhibitionAnalysis(exhibitionId);
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }

    @Operation(summary = "Получить статус асинхронной задачи")
    @ApiResponse(responseCode = "200", description = "Статус задачи возвращен")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректный или неизвестный taskId",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskInfoDto> getTaskStatus(@PathVariable UUID taskId) {
        return ResponseEntity.ok(exhibitionAsyncService.getTaskStatus(taskId));
    }
}
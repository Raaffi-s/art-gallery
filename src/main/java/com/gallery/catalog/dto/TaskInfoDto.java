package com.gallery.catalog.dto;

import com.gallery.catalog.service.async.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO со статусом асинхронной задачи")
public record TaskInfoDto(
    @Schema(description = "ID задачи", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID taskId,

    @Schema(description = "Статус задачи", example = "RUNNING")
    TaskStatus status,

    @Schema(description = "Текстовое сообщение о состоянии задачи", example = "Exhibition analysis in progress")
    String message,

    @Schema(description = "Время создания задачи", example = "2026-05-12T04:05:00")
    LocalDateTime createdAt,

    @Schema(description = "Время завершения задачи", example = "2026-05-12T04:05:05")
    LocalDateTime completedAt
) {
}
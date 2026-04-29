package com.gallery.catalog.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Стандартный ответ об ошибке")
public record ErrorResponse(

    @Schema(description = "HTTP статус код", example = "404")
    int status,

    @Schema(description = "Краткое название ошибки", example = "Not Found")
    String error,

    @Schema(description = "Подробное сообщение об ошибке", example = "Painting not found: 1")
    String message,

    @Schema(description = "Путь запроса, на котором произошла ошибка", example = "/api/paintings/1")
    String path,

    @Schema(description = "Дата и время возникновения ошибки", example = "2026-04-29T17:20:00")
    LocalDateTime timestamp

) {}
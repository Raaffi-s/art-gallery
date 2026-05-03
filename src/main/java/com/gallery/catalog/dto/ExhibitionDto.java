package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO выставки")
public record ExhibitionDto(

    @Schema(description = "Уникальный идентификатор выставки", example = "1")
    Long id,

    @Schema(description = "Название выставки", example = "Spring Exhibition")
    String title,

    @Schema(description = "Описание выставки", example = "Demo exhibition for bulk operations")
    String description,

    @Schema(description = "Дата и время начала выставки", example = "2026-05-04T10:00:00")
    LocalDateTime startDate,

    @Schema(description = "Дата и время окончания выставки", example = "2026-05-10T18:00:00")
    LocalDateTime endDate,

    @Schema(description = "Названия картин, входящих в выставку")
    Set<String> paintingTitles,

    @Schema(description = "Количество картин в выставке", example = "2")
    Integer paintingsCount
) {}
package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO выставки")
public record ExhibitionDto(

    @Schema(
        description = "Идентификатор выставки",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    Long id,

    @Schema(
        description = "Название выставки",
        example = "Шедевры Ренессанса"
    )
    String title,

    @Schema(
        description = "Описание выставки",
        example = "Коллекция известных произведений эпохи Возрождения"
    )
    String description,

    @Schema(
        description = "Дата и время начала выставки",
        example = "2026-05-01T10:00:00"
    )
    LocalDateTime startDate,

    @Schema(
        description = "Дата и время окончания выставки",
        example = "2026-05-31T18:00:00"
    )
    LocalDateTime endDate,

    @Schema(
        description = "Названия картин, представленных на выставке",
        example = "[\"Мона Лиза\", \"Тайная вечеря\"]"
    )
    Set<String> paintingTitles,

    @Schema(
        description = "Количество картин на выставке",
        example = "2"
    )
    Integer paintingsCount

) {}
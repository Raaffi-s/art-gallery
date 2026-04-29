package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Картина в каталоге галереи")
public record PaintingDto(

    @Schema(
        description = "Идентификатор картины",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    Long id,

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    @Schema(
        description = "Название картины",
        example = "Мона Лиза"
    )
    String title,

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Schema(
        description = "Описание картины",
        example = "Портрет молодой женщины, написанный Леонардо да Винчи"
    )
    String description,

    @NotBlank(message = "Artist is required")
    @Schema(
        description = "Имя художника",
        example = "Леонардо да Винчи"
    )
    String artist,

    @Min(value = 1000, message = "Year must be at least 1000")
    @Max(value = 2026, message = "Year cannot be in the future")
    @Schema(
        description = "Год создания картины",
        example = "1503"
    )
    Integer year,

    @Positive(message = "Price must be positive")
    @Schema(
        description = "Цена картины",
        example = "1000000.00"
    )
    Double price,

    @Schema(
        description = "URL изображения картины",
        example = "https://example.com/images/mona-lisa.jpg"
    )
    String imageUrl,

    @Schema(
        description = "Техника исполнения",
        example = "Масло на дереве"
    )
    String technique,

    @Schema(
        description = "Название галереи, в которой выставлена картина",
        example = "Лувр"
    )
    String galleryName,

    @Schema(
        description = "Список тегов, связанных с картиной",
        example = "[\"Ренессанс\", \"Портрет\"]"
    )
    Set<String> tagNames

) {}
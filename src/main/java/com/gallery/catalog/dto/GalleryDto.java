package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO галереи")
public record GalleryDto(

    @Schema(
        description = "Идентификатор галереи",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    Long id,

    @NotBlank(message = "Gallery name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    @Schema(
        description = "Название галереи",
        example = "Лувр"
    )
    String name,

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Schema(
        description = "Описание галереи",
        example = "Одна из крупнейших художественных галерей мира"
    )
    String description,

    @NotBlank(message = "Owner name is required")
    @Schema(
        description = "Имя владельца галереи",
        example = "Иван Иванов"
    )
    String ownerName,

    @Schema(
        description = "Количество картин в галерее",
        example = "25"
    )
    Integer paintingsCount

) {}
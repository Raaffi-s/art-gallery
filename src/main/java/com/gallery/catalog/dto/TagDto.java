package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO тега")
public record TagDto(

    @Schema(
        description = "Идентификатор тега",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    Long id,

    @Schema(
        description = "Название тега",
        example = "Ренессанс"
    )
    String name,

    @Schema(
        description = "Описание тега",
        example = "Картины эпохи Возрождения"
    )
    String description

) {}
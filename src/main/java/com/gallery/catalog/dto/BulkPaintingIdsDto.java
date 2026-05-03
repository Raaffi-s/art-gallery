package com.gallery.catalog.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "DTO для массового добавления картин в выставку")
public record BulkPaintingIdsDto(

    @ArraySchema(
        schema = @Schema(
            description = "ID картины",
            example = "1"
        ),
        arraySchema = @Schema(
            description = "Список ID картин для массового добавления"
        )
    )
    List<Long> paintingIds
) {}
package com.gallery.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Базовый DTO с идентификатором")
public abstract class BaseDto {

    @Schema(
        description = "Идентификатор сущности",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
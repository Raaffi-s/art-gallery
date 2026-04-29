package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GalleryDto(
    Long id,

    @NotBlank(message = "Gallery name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    String name,

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    String description,

    @NotBlank(message = "Owner name is required")
    String ownerName,

    Integer paintingsCount
) {}
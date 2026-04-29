package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingDto(
    Long id,

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    String title,

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    String description,

    @NotBlank(message = "Artist is required")
    String artist,

    @Min(value = 1000, message = "Year must be at least 1000")
    @Max(value = 2026, message = "Year cannot be in the future")
    Integer year,

    @Positive(message = "Price must be positive")
    Double price,

    String imageUrl,
    String technique,
    String galleryName,
    Set<String> tagNames
) {}
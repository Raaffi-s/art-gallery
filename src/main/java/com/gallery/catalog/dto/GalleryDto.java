package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GalleryDto(
    Long id,
    String name,
    String description,
    String ownerName,
    Integer paintingsCount
) {}
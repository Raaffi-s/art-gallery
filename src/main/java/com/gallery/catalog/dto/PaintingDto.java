package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingDto(
    Long id,
    String title,
    String description,
    String artist,
    Integer year,
    Long price,
    String imageUrl,
    String technique,
    String galleryName,
    Set<String> tagNames
) {

}
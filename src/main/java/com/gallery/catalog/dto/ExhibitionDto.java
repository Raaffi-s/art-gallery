package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExhibitionDto(
    Long id,
    String title,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Set<String> paintingTitles,
    Integer paintingsCount
) {}
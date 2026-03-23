package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TagDto(
    Long id,
    String name,
    String description
) {}
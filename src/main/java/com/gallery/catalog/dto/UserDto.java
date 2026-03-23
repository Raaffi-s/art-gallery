package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
    Long id,
    String username,
    String email,
    String fullName,
    String avatarUrl,
    String bio,
    Integer paintingsCount,
    Integer galleriesCount
) {}
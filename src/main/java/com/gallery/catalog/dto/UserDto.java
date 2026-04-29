package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO пользователя")
public record UserDto(

    @Schema(
        description = "Идентификатор пользователя",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    Long id,

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(
        description = "Имя пользователя",
        example = "artlover123"
    )
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(
        description = "Email пользователя",
        example = "user@example.com"
    )
    String email,

    @Schema(
        description = "Полное имя пользователя",
        example = "Алексей Иванов"
    )
    String fullName,

    @Schema(
        description = "URL аватара пользователя",
        example = "https://example.com/avatars/user1.jpg"
    )
    String avatarUrl,

    @Schema(
        description = "Краткая информация о пользователе",
        example = "Коллекционер современного искусства"
    )
    String bio,

    @Schema(
        description = "Количество галерей пользователя",
        example = "3"
    )
    Integer galleriesCount

) {}
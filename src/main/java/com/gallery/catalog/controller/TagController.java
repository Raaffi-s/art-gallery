package com.gallery.catalog.controller;

import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.exception.ErrorResponse;
import com.gallery.catalog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "Получить список тегов (опционально по имени)")
    @ApiResponse(responseCode = "200", description = "Теги успешно получены")
    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(
        @RequestParam(required = false) String name
    ) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(List.of(tagService.getTagByName(name)));
        }
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @Operation(summary = "Получить тег по ID")
    @ApiResponse(responseCode = "200", description = "Тег найден")
    @ApiResponse(
        responseCode = "404",
        description = "Тег не найден",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @Operation(summary = "Создать новый тег")
    @ApiResponse(responseCode = "201", description = "Тег успешно создан")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные данные запроса",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "409",
        description = "Тег с таким именем уже существует",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto dto) {
        return new ResponseEntity<>(tagService.createTag(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить тег по ID")
    @ApiResponse(responseCode = "200", description = "Тег успешно обновлён")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные данные запроса",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Тег не найден",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "409",
        description = "Имя тега уже используется",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(
        @PathVariable Long id,
        @RequestBody TagDto dto
    ) {
        return ResponseEntity.ok(tagService.updateTag(id, dto));
    }

    @Operation(summary = "Удалить тег по ID")
    @ApiResponse(responseCode = "204", description = "Тег успешно удалён")
    @ApiResponse(
        responseCode = "404",
        description = "Тег не найден",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
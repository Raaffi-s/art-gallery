package com.gallery.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат демонстрации race condition")
public record RaceConditionResultDto(
    @Schema(description = "Название сценария", example = "unsafe-counter")
    String mode,

    @Schema(description = "Количество потоков", example = "100")
    int threads,

    @Schema(description = "Количество инкрементов на поток", example = "1000")
    int incrementsPerThread,

    @Schema(description = "Ожидаемое итоговое значение", example = "100000")
    int expected,

    @Schema(description = "Фактическое итоговое значение", example = "97342")
    int actual
) {
}

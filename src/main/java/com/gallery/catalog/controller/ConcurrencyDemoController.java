package com.gallery.catalog.controller;

import com.gallery.catalog.dto.RaceConditionResultDto;
import com.gallery.catalog.exception.ErrorResponse;
import com.gallery.catalog.service.async.RaceConditionDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concurrency-demo")
public class ConcurrencyDemoController {

    private static final int DEFAULT_THREADS = 100;
    private static final int DEFAULT_INCREMENTS = 1000;

    private final RaceConditionDemoService raceConditionDemoService;

    public ConcurrencyDemoController(RaceConditionDemoService raceConditionDemoService) {
        this.raceConditionDemoService = raceConditionDemoService;
    }

    @Operation(summary = "Демонстрация race condition с обычным int")
    @ApiResponse(responseCode = "200", description = "Результат unsafe-сценария")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/race-condition/unsafe")
    public ResponseEntity<RaceConditionResultDto> runUnsafeDemo(
        @RequestParam(defaultValue = "" + DEFAULT_THREADS) int threads,
        @RequestParam(defaultValue = "" + DEFAULT_INCREMENTS) int increments
    ) {
        validateInput(threads, increments);
        return ResponseEntity.ok(
            raceConditionDemoService.runUnsafeDemo(threads, increments)
        );
    }

    @Operation(summary = "Решение race condition через AtomicInteger")
    @ApiResponse(responseCode = "200", description = "Результат atomic-сценария")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/race-condition/atomic")
    public ResponseEntity<RaceConditionResultDto> runAtomicDemo(
        @RequestParam(defaultValue = "" + DEFAULT_THREADS) int threads,
        @RequestParam(defaultValue = "" + DEFAULT_INCREMENTS) int increments
    ) {
        validateInput(threads, increments);
        return ResponseEntity.ok(
            raceConditionDemoService.runAtomicDemo(threads, increments)
        );
    }

    @Operation(summary = "Решение race condition через synchronized")
    @ApiResponse(responseCode = "200", description = "Результат synchronized-сценария")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/race-condition/synchronized")
    public ResponseEntity<RaceConditionResultDto> runSynchronizedDemo(
        @RequestParam(defaultValue = "" + DEFAULT_THREADS) int threads,
        @RequestParam(defaultValue = "" + DEFAULT_INCREMENTS) int increments
    ) {
        validateInput(threads, increments);
        return ResponseEntity.ok(
            raceConditionDemoService.runSynchronizedDemo(threads, increments)
        );
    }

    private void validateInput(int threads, int increments) {
        if (threads < 50) {
            throw new IllegalArgumentException("Threads count must be at least 50");
        }
        if (increments <= 0) {
            throw new IllegalArgumentException("Increments must be greater than 0");
        }
    }
}

package com.gallery.catalog.controller;

import com.gallery.catalog.service.async.AsyncTaskCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/async-demo")
public class AsyncDemoController {

    private final AsyncTaskCounterService asyncTaskCounterService;

    public AsyncDemoController(AsyncTaskCounterService asyncTaskCounterService) {
        this.asyncTaskCounterService = asyncTaskCounterService;
    }

    @Operation(summary = "Увеличить потокобезопасный счетчик")
    @ApiResponse(responseCode = "200", description = "Счетчик увеличен")
    @PostMapping("/counter/increment")
    public ResponseEntity<Map<String, Integer>> incrementCounter() {
        int value = asyncTaskCounterService.incrementAndGet();
        return ResponseEntity.ok(Map.of("counter", value));
    }

    @Operation(summary = "Получить текущее значение потокобезопасного счетчика")
    @ApiResponse(responseCode = "200", description = "Значение счетчика возвращено")
    @GetMapping("/counter")
    public ResponseEntity<Map<String, Integer>> getCounter() {
        return ResponseEntity.ok(Map.of("counter", asyncTaskCounterService.getValue()));
    }

    @Operation(summary = "Сбросить потокобезопасный счетчик")
    @ApiResponse(responseCode = "200", description = "Счетчик сброшен")
    @PostMapping("/counter/reset")
    public ResponseEntity<Map<String, Integer>> resetCounter() {
        asyncTaskCounterService.reset();
        return ResponseEntity.ok(Map.of("counter", 0));
    }
}
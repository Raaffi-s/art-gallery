package com.gallery.catalog.controller;

import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.service.TransactionDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final TransactionDemoService transactionDemoService;

    public DemoController(TransactionDemoService transactionDemoService) {
        this.transactionDemoService = transactionDemoService;
    }

    @Operation(summary = "Демонстрация N+1 проблемы")
    @ApiResponse(responseCode = "200", description = "Демо-эндпоинт N+1 доступен")
    @GetMapping("/n-plus-1")
    public ResponseEntity<String> showNPlusOneProblem() {
        return ResponseEntity.ok("N+1 problem demo endpoint");
    }

    @Operation(summary = "Создать галерею без транзакции")
    @ApiResponse(responseCode = "200", description = "Операция выполнена без транзакции")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping("/without-transaction")
    public ResponseEntity<String> createGalleryWithoutTransaction(
        @RequestBody GalleryDto dto
    ) {
        transactionDemoService.createGalleryWithoutTransaction(dto);
        return ResponseEntity.ok("Gallery creation without transaction completed");
    }

    @Operation(summary = "Создать галерею с транзакцией")
    @ApiResponse(responseCode = "200", description = "Операция выполнена с транзакцией")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping("/with-transaction")
    public ResponseEntity<String> createGalleryWithTransaction(
        @RequestBody GalleryDto dto
    ) {
        transactionDemoService.createGalleryWithTransaction(dto);
        return ResponseEntity.ok("Gallery creation with transaction completed");
    }
}
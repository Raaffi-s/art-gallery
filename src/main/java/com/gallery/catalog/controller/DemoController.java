package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.dto.TransactionRequest;
import com.gallery.catalog.service.PaintingService;
import com.gallery.catalog.service.TransactionDemoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final PaintingService paintingService;
    private final TransactionDemoService transactionDemoService;

    public DemoController(PaintingService paintingService,
                          TransactionDemoService transactionDemoService) {
        this.paintingService = paintingService;
        this.transactionDemoService = transactionDemoService;
    }

    @GetMapping("/n-plus-1")
    public ResponseEntity<List<PaintingDto>> demoNplus1() {
        List<PaintingDto> paintings = paintingService.getPaintingsWithNplus1Problem();
        return ResponseEntity.ok(paintings);
    }

    @PostMapping("/without-transaction")
    public ResponseEntity<String> demoWithoutTransaction(
        @RequestBody(required = false) TransactionRequest request) {
        try {
            transactionDemoService.createGalleryWithoutTransaction(request);
            return ResponseEntity.ok("Operation completed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage()
                    + " - Data partially saved in database!");
        }
    }

    @PostMapping("/with-transaction")
    public ResponseEntity<String> demoWithTransaction(
        @RequestBody(required = false) TransactionRequest request) {
        try {
            transactionDemoService.createGalleryWithTransaction(request);
            return ResponseEntity.ok("Operation completed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage()
                    + " - Data fully rolled back!");
        }
    }
}
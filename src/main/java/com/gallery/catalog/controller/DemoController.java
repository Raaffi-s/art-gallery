package com.gallery.catalog.controller;

import com.gallery.catalog.service.TransactionDemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final TransactionDemoService transactionDemoService;

    public DemoController(TransactionDemoService transactionDemoService) {
        this.transactionDemoService = transactionDemoService;
    }

    @GetMapping("/n-plus-1")
    public ResponseEntity<String> showNPlusOneProblem() {
        return ResponseEntity.ok(
            "N+1 problem demo endpoint"
        );
    }

    @PostMapping("/without-transaction")
    public ResponseEntity<String> createGalleryWithoutTransaction() {
        transactionDemoService.createGalleryWithoutTransaction();
        return ResponseEntity.ok(
            "Gallery creation without transaction completed"
        );
    }

    @PostMapping("/with-transaction")
    public ResponseEntity<String> createGalleryWithTransaction() {
        transactionDemoService.createGalleryWithTransaction();
        return ResponseEntity.ok(
            "Gallery creation with transaction completed"
        );
    }
}
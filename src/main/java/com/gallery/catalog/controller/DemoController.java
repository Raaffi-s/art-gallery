package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.service.PaintingService;
import com.gallery.catalog.service.TransactionDemoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final TransactionDemoService transactionDemoService;
    private final PaintingService paintingService;

    public DemoController(
        TransactionDemoService transactionDemoService,
        PaintingService paintingService
    ) {
        this.transactionDemoService = transactionDemoService;
        this.paintingService = paintingService;
    }

    @GetMapping("/n-plus-1")
    public ResponseEntity<List<PaintingDto>> showNPlusOneProblem() {
        return ResponseEntity.ok(
            paintingService.getPaintingsWithNplus1Problem()
        );
    }

    @GetMapping("/n-plus-1-fixed")
    public ResponseEntity<List<PaintingDto>> showSolvedNPlusOneProblem() {
        return ResponseEntity.ok(
            paintingService.getAllPaintings()
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
package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.service.PaintingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paintings")
public class PaintingController {
    private final PaintingService service;

    public PaintingController(PaintingService service) {
        this.service = service;
    }

    // GET с RequestParam: /api/paintings?artist=Ван Гог
    @GetMapping
    public List<PaintingDto> getPaintings(@RequestParam(required = false) String artist) {
        if (artist != null && !artist.isEmpty()) {
            return service.getPaintingsByArtist(artist);
        }
        return service.getAllPaintings();
    }

    // GET с PathVariable: /api/paintings/1
    @GetMapping("/{id}")
    public PaintingDto getPaintingById(@PathVariable Long id) {
        return service.getPaintingById(id);
    }
}

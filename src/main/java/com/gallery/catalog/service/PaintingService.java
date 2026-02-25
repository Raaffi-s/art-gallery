package com.gallery.catalog.service;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.repository.PaintingRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaintingService {
    private final PaintingRepository repository;

    public PaintingService(PaintingRepository repository) {
        this.repository = repository;
    }

    private PaintingDto convertToDto(Painting painting) {
        return new PaintingDto(
                painting.getId(),
                painting.getTitle(),
                painting.getArtist(),
                painting.getYear(),
                painting.getPrice()
        );
    }

    public List<PaintingDto> getAllPaintings() {
        List<PaintingDto> result = new ArrayList<>();
        for (Painting p : repository.findAll()) {
            result.add(convertToDto(p));
        }
        return result;
    }

    public PaintingDto getPaintingById(Long id) {
        Painting painting = repository.findById(id);
        return painting != null ? convertToDto(painting) : null;
    }

    public List<PaintingDto> getPaintingsByArtist(String artist) {
        List<PaintingDto> result = new ArrayList<>();
        for (Painting p : repository.findByArtist(artist)) {
            result.add(convertToDto(p));
        }
        return result;
    }
}
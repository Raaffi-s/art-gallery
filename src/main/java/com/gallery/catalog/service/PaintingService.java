package com.gallery.catalog.service;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.exception.PaintingNotFoundException;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.PaintingRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaintingService {

    private static final int CURRENT_YEAR = 2026;
    private static final int MIN_YEAR = 1000;

    private final PaintingRepository paintingRepository;

    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    private PaintingDto convertToDto(Painting painting) {
        PaintingDto dto = new PaintingDto();
        dto.setId(painting.getId());
        dto.setTitle(painting.getTitle());
        dto.setDescription(painting.getDescription());
        dto.setArtist(painting.getArtist());
        dto.setYear(painting.getYear());
        dto.setPrice(painting.getPrice());
        dto.setImageUrl(painting.getImageUrl());
        dto.setTechnique(painting.getTechnique());

        if (painting.getGallery() != null) {
            dto.setGalleryName(painting.getGallery().getName());
        }

        if (painting.getTags() != null && !painting.getTags().isEmpty()) {
            dto.setTagNames(
                painting.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    public List<PaintingDto> getAllPaintings() {
        return paintingRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    public PaintingDto getPaintingById(Long id) {
        Painting painting = paintingRepository.findWithDetailsById(id)
            .orElseThrow(() -> new PaintingNotFoundException(id));
        return convertToDto(painting);
    }

    public List<PaintingDto> getPaintingsByArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) {
            return List.of();
        }
        return paintingRepository.findByArtistContainingIgnoreCase(artist.trim())
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    public List<PaintingDto> getPaintingsWithNplus1Problem() {
        return paintingRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional
    public PaintingDto createPainting(PaintingDto dto) {
        validatePaintingDto(dto);

        Painting painting = new Painting();
        updatePaintingFromDto(painting, dto);

        Painting saved = paintingRepository.save(painting);
        return convertToDto(saved);
    }

    @Transactional
    public PaintingDto updatePainting(Long id, PaintingDto dto) {
        validatePaintingDto(dto);

        Painting painting = paintingRepository.findById(id)
            .orElseThrow(() -> new PaintingNotFoundException(id));

        updatePaintingFromDto(painting, dto);

        Painting updated = paintingRepository.save(painting);
        return convertToDto(updated);
    }

    @Transactional
    public void deletePainting(Long id) {
        if (!paintingRepository.existsById(id)) {
            throw new PaintingNotFoundException(id);
        }
        paintingRepository.deleteById(id);
    }

    private void validatePaintingDto(PaintingDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.getArtist() == null || dto.getArtist().trim().isEmpty()) {
            throw new IllegalArgumentException("Artist is required");
        }
        if (dto.getYear() != null
            && (dto.getYear() < MIN_YEAR || dto.getYear() > CURRENT_YEAR)) {

            throw new IllegalArgumentException(
                "Year must be between " + MIN_YEAR + " and " + CURRENT_YEAR
            );
        }
    }

    private void updatePaintingFromDto(Painting painting, PaintingDto dto) {
        painting.setTitle(dto.getTitle().trim());
        painting.setDescription(dto.getDescription() != null
            ? dto.getDescription().trim()
            : null);
        painting.setArtist(dto.getArtist().trim());
        painting.setYear(dto.getYear());
        painting.setPrice(dto.getPrice());
        painting.setImageUrl(dto.getImageUrl());
        painting.setTechnique(dto.getTechnique());
    }
}
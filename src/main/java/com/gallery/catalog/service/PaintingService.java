package com.gallery.catalog.service;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.PaintingRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;

    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    // ✅ Конвертация Entity → DTO
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

        if (painting.getUser() != null) {
            dto.setUserName(painting.getUser().getUsername());
        }

        if (painting.getGallery() != null) {
            dto.setGalleryName(painting.getGallery().getName());
        }

        if (painting.getTags() != null) {
            dto.setTagNames(painting.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet()));
        }

        return dto;
    }

    // ✅ GET все картины (без N+1)
    public List<PaintingDto> getAllPaintings() {
        return paintingRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // ✅ GET по ID
    public PaintingDto getPaintingById(Long id) {
        Painting painting = paintingRepository.findWithDetailsById(id)
            .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));
        return convertToDto(painting);
    }

    // ✅ GET по художнику
    public List<PaintingDto> getPaintingsByArtist(String artist) {
        return paintingRepository.findByArtistContainingIgnoreCase(artist)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // ✅ GET по художнику со всеми деталями
    public List<PaintingDto> getPaintingsByArtistWithDetails(String artist) {
        return paintingRepository.findByArtistWithDetails(artist)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // ⚠️ Демонстрация проблемы N+1
    public List<PaintingDto> getPaintingsWithNplus1Problem() {
        return paintingRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // ✅ CREATE
    @Transactional
    public PaintingDto createPainting(PaintingDto dto) {
        Painting painting = new Painting();
        painting.setTitle(dto.getTitle());
        painting.setDescription(dto.getDescription());
        painting.setArtist(dto.getArtist());
        painting.setYear(dto.getYear());
        painting.setPrice(dto.getPrice());
        painting.setImageUrl(dto.getImageUrl());
        painting.setTechnique(dto.getTechnique());

        Painting saved = paintingRepository.save(painting);
        return convertToDto(saved);
    }

    // ✅ UPDATE
    @Transactional
    public PaintingDto updatePainting(Long id, PaintingDto dto) {
        Painting painting = paintingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));

        painting.setTitle(dto.getTitle());
        painting.setDescription(dto.getDescription());
        painting.setArtist(dto.getArtist());
        painting.setYear(dto.getYear());
        painting.setPrice(dto.getPrice());
        painting.setImageUrl(dto.getImageUrl());
        painting.setTechnique(dto.getTechnique());

        Painting updated = paintingRepository.save(painting);
        return convertToDto(updated);
    }

    // ✅ DELETE
    @Transactional
    public void deletePainting(Long id) {
        if (!paintingRepository.existsById(id)) {
            throw new RuntimeException("Painting not found with id: " + id);
        }
        paintingRepository.deleteById(id);
    }
}
package com.gallery.catalog.service;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.exception.PaintingNotFoundException;
import com.gallery.catalog.exception.UserNotFoundException;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.PaintingRepository;
import com.gallery.catalog.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final UserRepository userRepository;

    public PaintingService(PaintingRepository paintingRepository,
                           UserRepository userRepository) {
        this.paintingRepository = paintingRepository;
        this.userRepository = userRepository;
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
        return paintingRepository.findByArtistContainingIgnoreCase(artist)
            .stream()
            .map(this::convertToDto)
            .toList();
    }

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

        if (dto.getUserName() != null) {
            User user = userRepository.findByUsername(dto.getUserName())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserName()));
            painting.setUser(user);
        }

        Painting saved = paintingRepository.save(painting);
        return convertToDto(saved);
    }

    @Transactional
    public PaintingDto updatePainting(Long id, PaintingDto dto) {
        Painting painting = paintingRepository.findById(id)
            .orElseThrow(() -> new PaintingNotFoundException(id));

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

    @Transactional
    public void deletePainting(Long id) {
        if (!paintingRepository.existsById(id)) {
            throw new PaintingNotFoundException(id);
        }
        paintingRepository.deleteById(id);
    }
}
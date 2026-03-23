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
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaintingService {

    private static final int CURRENT_YEAR = 2026;
    private static final int MIN_YEAR = 1000;

    private final PaintingRepository paintingRepository;
    private final UserRepository userRepository;

    public PaintingService(PaintingRepository paintingRepository,
                           UserRepository userRepository) {
        this.paintingRepository = paintingRepository;
        this.userRepository = userRepository;
    }

    private PaintingDto convertToDto(Painting painting) {
        Set<String> tagNames = null;
        if (painting.getTags() != null && !painting.getTags().isEmpty()) {
            tagNames = painting.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        }

        return new PaintingDto(
            painting.getId(),
            painting.getTitle(),
            painting.getDescription(),
            painting.getArtist(),
            painting.getYear(),
            painting.getPrice(),
            painting.getImageUrl(),
            painting.getTechnique(),
            painting.getUser() != null ? painting.getUser().getUsername() : null,
            painting.getGallery() != null ? painting.getGallery().getName() : null,
            tagNames
        );
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

        if (dto.userName() != null && !dto.userName().trim().isEmpty()) {
            User user = userRepository.findByUsername(dto.userName().trim())
                .orElseThrow(() -> new UserNotFoundException(dto.userName()));
            painting.setUser(user);
        }

        return convertToDto(paintingRepository.save(painting));
    }

    @Transactional
    public PaintingDto updatePainting(Long id, PaintingDto dto) {
        validatePaintingDto(dto);

        Painting painting = paintingRepository.findById(id)
            .orElseThrow(() -> new PaintingNotFoundException(id));

        updatePaintingFromDto(painting, dto);

        return convertToDto(paintingRepository.save(painting));
    }

    @Transactional
    public void deletePainting(Long id) {
        if (!paintingRepository.existsById(id)) {
            throw new PaintingNotFoundException(id);
        }
        paintingRepository.deleteById(id);
    }

    private void validatePaintingDto(PaintingDto dto) {
        if (dto.title() == null || dto.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.artist() == null || dto.artist().trim().isEmpty()) {
            throw new IllegalArgumentException("Artist is required");
        }
        if (dto.year() != null && (dto.year() < MIN_YEAR || dto.year() > CURRENT_YEAR)) {
            throw new IllegalArgumentException(
                "Year must be between " + MIN_YEAR + " and " + CURRENT_YEAR);
        }
    }

    private void updatePaintingFromDto(Painting painting, PaintingDto dto) {
        painting.setTitle(dto.title().trim());
        painting.setDescription(dto.description() != null ? dto.description().trim() : null);
        painting.setArtist(dto.artist().trim());
        painting.setYear(dto.year());
        painting.setPrice(dto.price());
        painting.setImageUrl(dto.imageUrl());
        painting.setTechnique(dto.technique());
    }
}
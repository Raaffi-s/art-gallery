package com.gallery.catalog.service;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.PaintingRepository;
import com.gallery.catalog.repository.TagRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaintingService {

    private static final int CURRENT_YEAR = 2026;
    private static final int MIN_YEAR = 1000;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final String PAINTING_NOT_FOUND = "Painting not found: ";
    private static final String TAG_NAME_REQUIRED = "Tag name is required";

    private final PaintingRepository paintingRepository;
    private final TagRepository tagRepository;

    public PaintingService(
        PaintingRepository paintingRepository,
        TagRepository tagRepository
    ) {
        this.paintingRepository = paintingRepository;
        this.tagRepository = tagRepository;
    }

    private PaintingDto convertToDto(Painting painting) {
        return new PaintingDto(
            painting.getId(),
            painting.getTitle(),
            painting.getDescription(),
            painting.getArtist(),
            painting.getYear(),
            painting.getPrice(),
            painting.getImageUrl(),
            painting.getTechnique(),
            painting.getGallery() != null ? painting.getGallery().getName() : null,
            painting.getTags() != null
                ? painting.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet())
                : Set.of()
        );
    }

    @Transactional(readOnly = true)
    public List<PaintingDto> getAllPaintings() {
        return paintingRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public PaintingDto getPaintingById(Long id) {
        Painting painting = paintingRepository.findWithDetailsById(id)
            .orElseThrow(() -> new IllegalArgumentException(PAINTING_NOT_FOUND + id));
        return convertToDto(painting);
    }

    @Transactional(readOnly = true)
    public List<PaintingDto> getPaintingsByArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) {
            return List.of();
        }

        return paintingRepository.findByArtistContainingIgnoreCase(artist.trim())
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
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
            .orElseThrow(() -> new IllegalArgumentException(PAINTING_NOT_FOUND + id));

        updatePaintingFromDto(painting, dto);

        Painting updated = paintingRepository.save(painting);
        return convertToDto(updated);
    }

    @Transactional
    public PaintingDto addTagToPainting(Long paintingId, String tagName) {
        Painting painting = paintingRepository.findWithDetailsById(paintingId)
            .orElseThrow(() -> new IllegalArgumentException(
                PAINTING_NOT_FOUND + paintingId));

        String normalizedTagName = normalizeTagName(tagName);

        Tag tag = tagRepository.findByName(normalizedTagName)
            .orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(normalizedTagName);
                return tagRepository.save(newTag);
            });

        if (painting.getTags() == null) {
            painting.setTags(new HashSet<>());
        }

        painting.getTags().add(tag);

        Painting updated = paintingRepository.save(painting);
        return convertToDto(updated);
    }

    @Transactional
    public PaintingDto removeTagFromPainting(Long paintingId, String tagName) {
        Painting painting = paintingRepository.findWithDetailsById(paintingId)
            .orElseThrow(() -> new IllegalArgumentException(
                PAINTING_NOT_FOUND + paintingId));

        String normalizedTagName = normalizeTagName(tagName);

        if (painting.getTags() != null) {
            painting.getTags().removeIf(tag ->
                normalizedTagName.equalsIgnoreCase(tag.getName()));
        }

        Painting updated = paintingRepository.save(painting);
        return convertToDto(updated);
    }

    @Transactional
    public void deletePainting(Long id) {
        if (!paintingRepository.existsById(id)) {
            throw new IllegalArgumentException(PAINTING_NOT_FOUND + id);
        }
        paintingRepository.deleteById(id);
    }

    private void validatePaintingDto(PaintingDto dto) {
        if (dto.title() == null || dto.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.title().length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title too long");
        }
        if (dto.artist() == null || dto.artist().trim().isEmpty()) {
            throw new IllegalArgumentException("Artist is required");
        }
        if (dto.description() != null && dto.description().length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description too long");
        }
        if (dto.year() != null && (dto.year() < MIN_YEAR || dto.year() > CURRENT_YEAR)) {
            throw new IllegalArgumentException(
                "Year must be between " + MIN_YEAR + " and " + CURRENT_YEAR
            );
        }
    }

    private void updatePaintingFromDto(Painting painting, PaintingDto dto) {
        painting.setTitle(dto.title().trim());
        painting.setDescription(
            dto.description() != null ? dto.description().trim() : null
        );
        painting.setArtist(dto.artist().trim());
        painting.setYear(dto.year());
        painting.setPrice(dto.price());
        painting.setImageUrl(dto.imageUrl());
        painting.setTechnique(dto.technique());
    }

    private String normalizeTagName(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new IllegalArgumentException(TAG_NAME_REQUIRED);
        }
        return tagName.trim();
    }
}
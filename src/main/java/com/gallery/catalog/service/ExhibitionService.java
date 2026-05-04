package com.gallery.catalog.service;

import com.gallery.catalog.dto.BulkPaintingIdsDto;
import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.exception.ExhibitionNotFoundException;
import com.gallery.catalog.model.Exhibition;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.repository.ExhibitionRepository;
import com.gallery.catalog.repository.PaintingRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExhibitionService {

    private static final String PAINTING_NOT_FOUND_MESSAGE = "Painting not found with id: ";

    private final ExhibitionRepository exhibitionRepository;
    private final PaintingRepository paintingRepository;

    public ExhibitionService(
        ExhibitionRepository exhibitionRepository,
        PaintingRepository paintingRepository
    ) {
        this.exhibitionRepository = exhibitionRepository;
        this.paintingRepository = paintingRepository;
    }

    private ExhibitionDto convertToDto(Exhibition exhibition) {
        Set<String> paintingTitles = null;
        Integer paintingsCount = null;

        if (exhibition.getPaintings() != null && !exhibition.getPaintings().isEmpty()) {
            paintingTitles = exhibition.getPaintings().stream()
                .map(Painting::getTitle)
                .collect(Collectors.toSet());
            paintingsCount = exhibition.getPaintings().size();
        }

        return new ExhibitionDto(
            exhibition.getId(),
            exhibition.getTitle(),
            exhibition.getDescription(),
            exhibition.getStartDate(),
            exhibition.getEndDate(),
            paintingTitles,
            paintingsCount
        );
    }

    @Transactional
    public ExhibitionDto addPaintingsToExhibitionBulk(Long exhibitionId, BulkPaintingIdsDto dto) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

        if (dto == null || dto.paintingIds() == null || dto.paintingIds().isEmpty()) {
            throw new IllegalArgumentException("Painting IDs list must not be empty");
        }

        List<Painting> paintings = dto.paintingIds().stream()
            .map(id -> paintingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PAINTING_NOT_FOUND_MESSAGE + id)))
            .toList();

        if (exhibition.getPaintings() == null) {
            exhibition.setPaintings(new HashSet<>());
        }

        exhibition.getPaintings().addAll(paintings);

        Exhibition updated = exhibitionRepository.save(exhibition);
        return convertToDto(updated);
    }

    public ExhibitionDto addPaintingsToExhibitionBulkWithoutTransactional(
        Long exhibitionId,
        BulkPaintingIdsDto dto
    ) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

        if (dto == null || dto.paintingIds() == null || dto.paintingIds().isEmpty()) {
            throw new IllegalArgumentException("Painting IDs list must not be empty");
        }

        if (exhibition.getPaintings() == null) {
            exhibition.setPaintings(new HashSet<>());
        }

        for (Long paintingId : dto.paintingIds()) {
            Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new IllegalArgumentException(PAINTING_NOT_FOUND_MESSAGE + paintingId));

            exhibition.getPaintings().add(painting);
            exhibitionRepository.save(exhibition);
        }

        return convertToDto(exhibition);
    }

    @Transactional(readOnly = true)
    public List<ExhibitionDto> getAllExhibitions() {
        return exhibitionRepository.findAllByOrderByStartDateDesc()
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public ExhibitionDto getExhibitionById(Long id) {
        Exhibition exhibition = exhibitionRepository.findById(id)
            .orElseThrow(() -> new ExhibitionNotFoundException(id.toString()));
        return convertToDto(exhibition);
    }

    @Transactional
    public ExhibitionDto createExhibition(ExhibitionDto dto) {
        validateExhibitionDto(dto);

        Exhibition exhibition = new Exhibition();
        updateExhibitionFromDto(exhibition, dto);

        if (dto.paintingTitles() != null && !dto.paintingTitles().isEmpty()) {
            Set<Painting> paintings = dto.paintingTitles().stream()
                .map(title -> paintingRepository.findAll().stream()
                    .filter(p -> p.getTitle().equalsIgnoreCase(title))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Painting not found: " + title)))
                .collect(Collectors.toSet());
            exhibition.setPaintings(paintings);
        }

        return convertToDto(exhibitionRepository.save(exhibition));
    }

    @Transactional
    public ExhibitionDto updateExhibition(Long id, ExhibitionDto dto) {
        validateExhibitionDto(dto);

        Exhibition exhibition = exhibitionRepository.findById(id)
            .orElseThrow(() -> new ExhibitionNotFoundException(id.toString()));

        updateExhibitionFromDto(exhibition, dto);

        return convertToDto(exhibitionRepository.save(exhibition));
    }

    @Transactional
    public void deleteExhibition(Long id) {
        if (!exhibitionRepository.existsById(id)) {
            throw new ExhibitionNotFoundException(id.toString());
        }
        exhibitionRepository.deleteById(id);
    }

    @Transactional
    public ExhibitionDto addPaintingToExhibition(Long exhibitionId, Long paintingId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

        Painting painting = paintingRepository.findById(paintingId)
            .orElseThrow(() -> new IllegalArgumentException(PAINTING_NOT_FOUND_MESSAGE + paintingId));

        if (exhibition.getPaintings() == null) {
            exhibition.setPaintings(new HashSet<>());
        }

        exhibition.getPaintings().add(painting);
        return convertToDto(exhibitionRepository.save(exhibition));
    }

    @Transactional
    public ExhibitionDto removePaintingFromExhibition(Long exhibitionId, Long paintingId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

        if (exhibition.getPaintings() != null) {
            exhibition.getPaintings().removeIf(p -> p.getId().equals(paintingId));
        }

        return convertToDto(exhibitionRepository.save(exhibition));
    }

    private void validateExhibitionDto(ExhibitionDto dto) {
        if (dto.title() == null || dto.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.startDate() != null && dto.endDate() != null
            && dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    private void updateExhibitionFromDto(Exhibition exhibition, ExhibitionDto dto) {
        exhibition.setTitle(dto.title().trim());
        exhibition.setDescription(dto.description() != null ? dto.description().trim() : null);
        exhibition.setStartDate(dto.startDate());
        exhibition.setEndDate(dto.endDate());
    }
}
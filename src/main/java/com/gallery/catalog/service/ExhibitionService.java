package com.gallery.catalog.service;

import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.model.Exhibition;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.repository.ExhibitionRepository;
import com.gallery.catalog.repository.PaintingRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExhibitionService {
    private String exhibitionNotFound = "Exhibition not found";

    private final ExhibitionRepository exhibitionRepository;
    private final PaintingRepository paintingRepository;

    public ExhibitionService(ExhibitionRepository exhibitionRepository,
                             PaintingRepository paintingRepository) {
        this.exhibitionRepository = exhibitionRepository;
        this.paintingRepository = paintingRepository;
    }

    private ExhibitionDto convertToDto(Exhibition exhibition) {
        ExhibitionDto dto = new ExhibitionDto();
        dto.setId(exhibition.getId());
        dto.setTitle(exhibition.getTitle());
        dto.setDescription(exhibition.getDescription());
        dto.setStartDate(exhibition.getStartDate());
        dto.setEndDate(exhibition.getEndDate());

        if (exhibition.getPaintings() != null && !exhibition.getPaintings().isEmpty()) {
            dto.setPaintingTitles(exhibition.getPaintings().stream()
                .map(Painting::getTitle)
                .collect(Collectors.toSet()));
            dto.setPaintingsCount(exhibition.getPaintings().size());
        }

        return dto;
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
            .orElseThrow(() -> new IllegalArgumentException(exhibitionNotFound + id));
        return convertToDto(exhibition);
    }

    @Transactional
    public ExhibitionDto createExhibition(ExhibitionDto dto) {
        validateExhibitionDto(dto);

        Exhibition exhibition = new Exhibition();
        updateExhibitionFromDto(exhibition, dto);

        if (dto.getPaintingTitles() != null && !dto.getPaintingTitles().isEmpty()) {
            Set<Painting> paintings = dto.getPaintingTitles().stream()
                .map(title -> paintingRepository.findAll().stream()
                    .filter(p -> p.getTitle().equalsIgnoreCase(title))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Painting not found: " + title)))
                .collect(Collectors.toSet());
            exhibition.setPaintings(paintings);
        }

        Exhibition saved = exhibitionRepository.save(exhibition);
        return convertToDto(saved);
    }

    @Transactional
    public ExhibitionDto updateExhibition(Long id, ExhibitionDto dto) {
        validateExhibitionDto(dto);

        Exhibition exhibition = exhibitionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(exhibitionNotFound + id));

        updateExhibitionFromDto(exhibition, dto);

        Exhibition updated = exhibitionRepository.save(exhibition);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteExhibition(Long id) {
        if (!exhibitionRepository.existsById(id)) {
            throw new IllegalArgumentException(exhibitionNotFound + id);
        }
        exhibitionRepository.deleteById(id);
    }

    @Transactional
    public ExhibitionDto addPaintingToExhibition(Long exhibitionId, Long paintingId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Exhibition not found: " + exhibitionId));

        Painting painting = paintingRepository.findById(paintingId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Painting not found: " + paintingId));

        exhibition.getPaintings().add(painting);
        Exhibition updated = exhibitionRepository.save(exhibition);
        return convertToDto(updated);
    }

    @Transactional
    public ExhibitionDto removePaintingFromExhibition(Long exhibitionId, Long paintingId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Exhibition not found: " + exhibitionId));

        exhibition.getPaintings().removeIf(p -> p.getId().equals(paintingId));
        Exhibition updated = exhibitionRepository.save(exhibition);
        return convertToDto(updated);
    }

    private void validateExhibitionDto(ExhibitionDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.getStartDate() != null && dto.getEndDate() != null
            && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    private void updateExhibitionFromDto(Exhibition exhibition, ExhibitionDto dto) {
        exhibition.setTitle(dto.getTitle().trim());
        exhibition.setDescription(
            dto.getDescription() != null ? dto.getDescription().trim() : null);
        exhibition.setStartDate(dto.getStartDate());
        exhibition.setEndDate(dto.getEndDate());
    }
}

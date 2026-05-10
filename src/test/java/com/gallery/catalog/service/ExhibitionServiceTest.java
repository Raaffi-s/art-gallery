package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.BulkPaintingIdsDto;
import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.exception.ExhibitionNotFoundException;
import com.gallery.catalog.model.Exhibition;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.repository.ExhibitionRepository;
import com.gallery.catalog.repository.PaintingRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExhibitionServiceTest {

    @Mock
    private ExhibitionRepository exhibitionRepository;

    @Mock
    private PaintingRepository paintingRepository;

    @InjectMocks
    private ExhibitionService exhibitionService;

    @Test
    void addPaintingsToExhibitionBulkAddsPaintingsAndInitializesSet() {
        Exhibition exhibition = exhibition(1L, "Expo");
        exhibition.setPaintings(null);
        Painting first = painting(10L, "First");
        Painting second = painting(11L, "Second");
        when(exhibitionRepository.findById(1L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(10L)).thenReturn(Optional.of(first));
        when(paintingRepository.findById(11L)).thenReturn(Optional.of(second));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.addPaintingsToExhibitionBulk(
            1L, new BulkPaintingIdsDto(List.of(10L, 11L))
        );

        assertThat(result.paintingTitles()).containsExactlyInAnyOrder("First", "Second");
        assertThat(result.paintingsCount()).isEqualTo(2);
    }

    @Test
    void addPaintingsToExhibitionBulkThrowsWhenExhibitionMissing() {
        when(exhibitionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulk(
            2L, new BulkPaintingIdsDto(List.of(1L))
        ))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("2");
    }

    @Test
    void addPaintingsToExhibitionBulkRequiresIds() {
        Exhibition exhibition = exhibition(3L, "Expo");
        when(exhibitionRepository.findById(3L)).thenReturn(Optional.of(exhibition));

        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulk(3L, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting IDs list must not be empty");
        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulk(
            3L, new BulkPaintingIdsDto(null)
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting IDs list must not be empty");
        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulk(
            3L, new BulkPaintingIdsDto(List.of())
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting IDs list must not be empty");
    }

    @Test
    void addPaintingsToExhibitionBulkThrowsWhenPaintingMissing() {
        Exhibition exhibition = exhibition(4L, "Expo");
        when(exhibitionRepository.findById(4L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulk(
            4L, new BulkPaintingIdsDto(List.of(99L))
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found with id: 99");
    }

    @Test
    void addPaintingsToExhibitionBulkUsesExistingPaintingSet() {
        Exhibition exhibition = exhibition(24L, "Expo");
        exhibition.setPaintings(new HashSet<>(Set.of(painting(1L, "Existing"))));
        when(exhibitionRepository.findById(24L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(2L)).thenReturn(Optional.of(painting(2L, "Added")));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.addPaintingsToExhibitionBulk(
            24L, new BulkPaintingIdsDto(List.of(2L))
        );

        assertThat(result.paintingTitles()).containsExactlyInAnyOrder("Existing", "Added");
    }

    @Test
    void addPaintingsWithoutTransactionalSavesAfterEachPainting() {
        Exhibition exhibition = exhibition(5L, "Expo");
        exhibition.setPaintings(null);
        when(exhibitionRepository.findById(5L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(1L)).thenReturn(Optional.of(painting(1L, "A")));
        when(paintingRepository.findById(2L)).thenReturn(Optional.of(painting(2L, "B")));

        ExhibitionDto result = exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(
            5L, new BulkPaintingIdsDto(List.of(1L, 2L))
        );

        assertThat(result.paintingsCount()).isEqualTo(2);
        verify(exhibitionRepository, times(2)).save(exhibition);
    }

    @Test
    void addPaintingsWithoutTransactionalThrowsWhenInvalid() {
        Exhibition exhibition = exhibition(6L, "Expo");
        when(exhibitionRepository.findById(6L)).thenReturn(Optional.of(exhibition));

        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(6L, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting IDs list must not be empty");
        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(
            6L, new BulkPaintingIdsDto(null)
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting IDs list must not be empty");
        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(
            6L, new BulkPaintingIdsDto(List.of())
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting IDs list must not be empty");
    }

    @Test
    void addPaintingsWithoutTransactionalThrowsWhenMissing() {
        when(exhibitionRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(
            7L, new BulkPaintingIdsDto(List.of(1L))
        ))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("7");
    }

    @Test
    void addPaintingsWithoutTransactionalThrowsWhenPaintingMissing() {
        Exhibition exhibition = exhibition(8L, "Expo");
        when(exhibitionRepository.findById(8L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(
            8L, new BulkPaintingIdsDto(List.of(3L))
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found with id: 3");
    }

    @Test
    void getAllExhibitionsReturnsDtosWithNullPaintingInfoForEmptySets() {
        Exhibition exhibition = exhibition(9L, "Expo");
        exhibition.setPaintings(Set.of());
        when(exhibitionRepository.findAllByOrderByStartDateDesc()).thenReturn(List.of(exhibition));

        ExhibitionDto result = exhibitionService.getAllExhibitions().get(0);

        assertThat(result.paintingTitles()).isNull();
        assertThat(result.paintingsCount()).isNull();
    }

    @Test
    void getExhibitionByIdReturnsDto() {
        Exhibition exhibition = exhibition(10L, "Expo");
        exhibition.setPaintings(new HashSet<>(Set.of(painting(1L, "A"))));
        when(exhibitionRepository.findById(10L)).thenReturn(Optional.of(exhibition));

        ExhibitionDto result = exhibitionService.getExhibitionById(10L);

        assertThat(result.paintingTitles()).containsExactly("A");
    }

    @Test
    void getExhibitionByIdThrowsWhenMissing() {
        when(exhibitionRepository.findById(11L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.getExhibitionById(11L))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("11");
    }

    @Test
    void createExhibitionSavesWithoutPaintings() {
        when(exhibitionRepository.save(org.mockito.ArgumentMatchers.any(Exhibition.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ExhibitionDto result = exhibitionService.createExhibition(dto(" Expo ", " Desc ", Set.of()));

        assertThat(result.title()).isEqualTo("Expo");
        assertThat(result.description()).isEqualTo("Desc");
    }

    @Test
    void createExhibitionAllowsNullDescriptionAndNullPaintingTitles() {
        when(exhibitionRepository.save(org.mockito.ArgumentMatchers.any(Exhibition.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ExhibitionDto result = exhibitionService.createExhibition(dto("Expo", null, null));

        assertThat(result.description()).isNull();
    }

    @Test
    void createExhibitionLinksPaintingsByTitleIgnoringCase() {
        Painting painting = painting(12L, "Mona Lisa");
        when(paintingRepository.findAll()).thenReturn(List.of(painting));
        when(exhibitionRepository.save(org.mockito.ArgumentMatchers.any(Exhibition.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ExhibitionDto result = exhibitionService.createExhibition(
            dto("Expo", null, Set.of("mona lisa"))
        );

        assertThat(result.paintingTitles()).containsExactly("Mona Lisa");
    }

    @Test
    void createExhibitionThrowsWhenPaintingTitleMissing() {
        when(paintingRepository.findAll()).thenReturn(List.of(painting(13L, "A")));

        assertThatThrownBy(() -> exhibitionService.createExhibition(
            dto("Expo", null, Set.of("B"))
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found: B");
    }

    @Test
    void updateExhibitionUpdatesExistingEntity() {
        Exhibition exhibition = exhibition(14L, "Old");
        when(exhibitionRepository.findById(14L)).thenReturn(Optional.of(exhibition));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.updateExhibition(14L, dto(" New ", null, null));

        assertThat(result.title()).isEqualTo("New");
        assertThat(result.description()).isNull();
    }

    @Test
    void updateExhibitionThrowsWhenMissing() {
        when(exhibitionRepository.findById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.updateExhibition(15L, dto("Expo", null, null)))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("15");
    }

    @Test
    void deleteExhibitionDeletesWhenExists() {
        when(exhibitionRepository.existsById(16L)).thenReturn(true);

        exhibitionService.deleteExhibition(16L);

        verify(exhibitionRepository).deleteById(16L);
    }

    @Test
    void deleteExhibitionThrowsWhenMissing() {
        when(exhibitionRepository.existsById(17L)).thenReturn(false);

        assertThatThrownBy(() -> exhibitionService.deleteExhibition(17L))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("17");
    }

    @Test
    void addPaintingToExhibitionAddsPaintingAndInitializesSet() {
        Exhibition exhibition = exhibition(18L, "Expo");
        exhibition.setPaintings(null);
        when(exhibitionRepository.findById(18L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(1L)).thenReturn(Optional.of(painting(1L, "A")));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.addPaintingToExhibition(18L, 1L);

        assertThat(result.paintingTitles()).containsExactly("A");
    }

    @Test
    void addPaintingToExhibitionThrowsWhenExhibitionMissing() {
        when(exhibitionRepository.findById(19L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.addPaintingToExhibition(19L, 1L))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("19");
    }

    @Test
    void addPaintingToExhibitionThrowsWhenPaintingMissing() {
        Exhibition exhibition = exhibition(20L, "Expo");
        when(exhibitionRepository.findById(20L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.addPaintingToExhibition(20L, 2L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found with id: 2");
    }

    @Test
    void addPaintingToExhibitionUsesExistingPaintingSet() {
        Exhibition exhibition = exhibition(25L, "Expo");
        exhibition.setPaintings(new HashSet<>(Set.of(painting(1L, "Existing"))));
        when(exhibitionRepository.findById(25L)).thenReturn(Optional.of(exhibition));
        when(paintingRepository.findById(2L)).thenReturn(Optional.of(painting(2L, "Added")));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.addPaintingToExhibition(25L, 2L);

        assertThat(result.paintingTitles()).containsExactlyInAnyOrder("Existing", "Added");
    }

    @Test
    void removePaintingFromExhibitionRemovesWhenPresent() {
        Exhibition exhibition = exhibition(21L, "Expo");
        exhibition.setPaintings(new HashSet<>(Set.of(painting(1L, "A"), painting(2L, "B"))));
        when(exhibitionRepository.findById(21L)).thenReturn(Optional.of(exhibition));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.removePaintingFromExhibition(21L, 1L);

        assertThat(result.paintingTitles()).containsExactly("B");
    }

    @Test
    void removePaintingFromExhibitionAllowsNullPaintings() {
        Exhibition exhibition = exhibition(22L, "Expo");
        exhibition.setPaintings(null);
        when(exhibitionRepository.findById(22L)).thenReturn(Optional.of(exhibition));
        when(exhibitionRepository.save(exhibition)).thenReturn(exhibition);

        ExhibitionDto result = exhibitionService.removePaintingFromExhibition(22L, 1L);

        assertThat(result.paintingTitles()).isNull();
    }

    @Test
    void removePaintingFromExhibitionThrowsWhenMissing() {
        when(exhibitionRepository.findById(23L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exhibitionService.removePaintingFromExhibition(23L, 1L))
            .isInstanceOf(ExhibitionNotFoundException.class)
            .hasMessage("23");
    }

    @Test
    void validateExhibitionRequiresTitle() {
        assertThatThrownBy(() -> exhibitionService.createExhibition(dto(null, null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
        assertThatThrownBy(() -> exhibitionService.createExhibition(dto(" ", null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");

        verifyNoInteractions(exhibitionRepository, paintingRepository);
    }

    @Test
    void validateExhibitionRejectsEndBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 7, 10, 0);
        LocalDateTime end = start.minusDays(1);
        ExhibitionDto dto = new ExhibitionDto(null, "Expo", null, start, end, null, null);

        assertThatThrownBy(() -> exhibitionService.createExhibition(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("End date must be after start date");
    }

    @Test
    void validateExhibitionAllowsMissingStartOrEndDate() {
        when(exhibitionRepository.save(org.mockito.ArgumentMatchers.any(Exhibition.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime date = LocalDateTime.of(2026, 5, 7, 10, 0);
        ExhibitionDto withoutStart = new ExhibitionDto(null, "Expo", null, null, date, null, null);
        ExhibitionDto withoutEnd = new ExhibitionDto(null, "Expo", null, date, null, null, null);

        ExhibitionDto first = exhibitionService.createExhibition(withoutStart);
        ExhibitionDto second = exhibitionService.createExhibition(withoutEnd);

        assertThat(first.startDate()).isNull();
        assertThat(first.endDate()).isEqualTo(date);
        assertThat(second.startDate()).isEqualTo(date);
        assertThat(second.endDate()).isNull();
    }

    private static ExhibitionDto dto(String title, String description, Set<String> paintingTitles) {
        LocalDateTime start = LocalDateTime.of(2026, 5, 7, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 8, 10, 0);
        return new ExhibitionDto(null, title, description, start, end, paintingTitles, null);
    }

    private static Exhibition exhibition(Long id, String title) {
        Exhibition exhibition = new Exhibition();
        exhibition.setId(id);
        exhibition.setTitle(title);
        exhibition.setDescription("Description");
        exhibition.setStartDate(LocalDateTime.of(2026, 5, 7, 10, 0));
        exhibition.setEndDate(LocalDateTime.of(2026, 5, 8, 10, 0));
        return exhibition;
    }

    private static Painting painting(Long id, String title) {
        Painting painting = new Painting();
        painting.setId(id);
        painting.setTitle(title);
        painting.setArtist("Artist");
        return painting;
    }
}

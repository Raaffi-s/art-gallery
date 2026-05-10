package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.GalleryRepository;
import com.gallery.catalog.repository.PaintingRepository;
import com.gallery.catalog.repository.TagRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class PaintingServiceTest {

    @Mock
    private PaintingRepository paintingRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private GalleryRepository galleryRepository;

    @InjectMocks
    private PaintingService paintingService;

    @Test
    void getAllPaintingsReturnsDtos() {
        Painting painting = painting(1L, "Mona", "Leonardo");
        painting.setGallery(gallery("Louvre"));
        painting.setTags(Set.of(tag("Portrait")));
        when(paintingRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(painting));

        List<PaintingDto> result = paintingService.getAllPaintings();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).galleryName()).isEqualTo("Louvre");
        assertThat(result.get(0).tagNames()).containsExactly("Portrait");
    }

    @Test
    void getPaintingByIdReturnsDtoWithNullGalleryAndEmptyTags() {
        Painting painting = painting(2L, "Untitled", "Artist");
        painting.setGallery(null);
        painting.setTags(null);
        when(paintingRepository.findWithDetailsById(2L)).thenReturn(Optional.of(painting));

        PaintingDto result = paintingService.getPaintingById(2L);

        assertThat(result.galleryName()).isNull();
        assertThat(result.tagNames()).isEmpty();
    }

    @Test
    void getPaintingByIdThrowsWhenMissing() {
        when(paintingRepository.findWithDetailsById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paintingService.getPaintingById(3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found: 3");
    }

    @Test
    void getPaintingsByArtistReturnsEmptyForNullOrBlankArtist() {
        assertThat(paintingService.getPaintingsByArtist(null)).isEmpty();
        assertThat(paintingService.getPaintingsByArtist(" ")).isEmpty();

        verify(paintingRepository, never()).findByArtistContainingIgnoreCase(any());
    }

    @Test
    void getPaintingsByArtistTrimsInput() {
        when(paintingRepository.findByArtistContainingIgnoreCase("Vincent"))
            .thenReturn(List.of(painting(4L, "Starry", "Vincent")));

        List<PaintingDto> result = paintingService.getPaintingsByArtist(" Vincent ");

        assertThat(result).extracting(PaintingDto::title).containsExactly("Starry");
    }

    @Test
    void getPaintingsWithNplus1ProblemMapsAllPaintings() {
        when(paintingRepository.findAll()).thenReturn(List.of(painting(5L, "A", "B")));

        List<PaintingDto> result = paintingService.getPaintingsWithNplus1Problem();

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaintingsByGalleryNameUsesTrimmedName() {
        when(paintingRepository.findByGalleryName("Louvre"))
            .thenReturn(List.of(painting(6L, "A", "B")));

        List<PaintingDto> result = paintingService.getPaintingsByGalleryName(" Louvre ");

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaintingsByGalleryNameNativeUsesTrimmedName() {
        when(paintingRepository.findByGalleryNameNative("Louvre"))
            .thenReturn(List.of(painting(7L, "A", "B")));

        List<PaintingDto> result = paintingService.getPaintingsByGalleryNameNative(" Louvre ");

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaintingsByGalleryNamePagedMapsPage() {
        Page<Painting> page = new PageImpl<>(List.of(painting(8L, "A", "B")));
        when(paintingRepository.findByGalleryNamePaged(eq("Louvre"), eq(PageRequest.of(1, 5))))
            .thenReturn(page);

        Page<PaintingDto> result = paintingService.getPaintingsByGalleryNamePaged(" Louvre ", 1, 5);

        assertThat(result.getContent()).extracting(PaintingDto::id).containsExactly(8L);
    }

    @Test
    void getPaintingsByGalleryNameCachedCachesByCaseInsensitiveKey() {
        Page<Painting> page = new PageImpl<>(List.of(painting(9L, "A", "B")));
        when(paintingRepository.findByGalleryNamePaged(eq("Louvre"), eq(PageRequest.of(0, 10))))
            .thenReturn(page);

        List<PaintingDto> first = paintingService.getPaintingsByGalleryNameCached(" Louvre ", 0, 10);
        List<PaintingDto> second = paintingService.getPaintingsByGalleryNameCached("louvre", 0, 10);

        assertThat(first).isSameAs(second);
        verify(paintingRepository).findByGalleryNamePaged("Louvre", PageRequest.of(0, 10));
    }

    @Test
    void createPaintingSavesAndClearsCache() {
        Gallery gallery = gallery("Louvre");
        when(galleryRepository.findByName("Louvre")).thenReturn(Optional.of(gallery));
        when(paintingRepository.save(any(Painting.class))).thenAnswer(invocation -> {
            Painting painting = invocation.getArgument(0);
            painting.setId(10L);
            return painting;
        });

        PaintingDto result = paintingService.createPainting(dto(" Title ", " Artist ", " Louvre "));

        assertThat(result.title()).isEqualTo("Title");
        assertThat(result.galleryName()).isEqualTo("Louvre");
    }

    @Test
    void createPaintingClearsGalleryWhenGalleryNameMissing() {
        when(paintingRepository.save(any(Painting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaintingDto nullGallery = paintingService.createPainting(dto("Title", "Artist", null));
        PaintingDto blankGallery = paintingService.createPainting(dto("Title", "Artist", " "));

        assertThat(nullGallery.galleryName()).isNull();
        assertThat(blankGallery.galleryName()).isNull();
        verifyNoInteractions(galleryRepository);
    }

    @Test
    void createPaintingAllowsNullDescriptionAndYear() {
        when(paintingRepository.save(any(Painting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PaintingDto dto = new PaintingDto(
            null, "Title", null, "Artist", null, 10.0, null, null, null, Set.of()
        );

        PaintingDto result = paintingService.createPainting(dto);

        assertThat(result.description()).isNull();
        assertThat(result.year()).isNull();
    }

    @Test
    void createPaintingLeavesGalleryNullWhenGalleryIsUnknown() {
        when(galleryRepository.findByName("Unknown")).thenReturn(Optional.empty());
        when(paintingRepository.save(any(Painting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaintingDto result = paintingService.createPainting(dto("Title", "Artist", "Unknown"));

        assertThat(result.galleryName()).isNull();
    }

    @Test
    void updatePaintingUpdatesExistingPainting() {
        Painting existing = painting(11L, "Old", "Old artist");
        when(paintingRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(paintingRepository.save(existing)).thenReturn(existing);

        PaintingDto result = paintingService.updatePainting(11L, dto(" New ", " Artist ", null));

        assertThat(result.title()).isEqualTo("New");
        assertThat(existing.getArtist()).isEqualTo("Artist");
    }

    @Test
    void updatePaintingThrowsWhenMissing() {
        when(paintingRepository.findById(12L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paintingService.updatePainting(12L, dto("Title", "Artist", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found: 12");
    }

    @Test
    void addTagToPaintingUsesExistingTag() {
        Painting painting = painting(13L, "A", "B");
        painting.setTags(new HashSet<>());
        Tag tag = tag("Modern");
        when(paintingRepository.findWithDetailsById(13L)).thenReturn(Optional.of(painting));
        when(tagRepository.findByName("Modern")).thenReturn(Optional.of(tag));
        when(paintingRepository.save(painting)).thenReturn(painting);

        PaintingDto result = paintingService.addTagToPainting(13L, " Modern ");

        assertThat(result.tagNames()).containsExactly("Modern");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void addTagToPaintingCreatesTagAndInitializesTagSet() {
        Painting painting = painting(14L, "A", "B");
        painting.setTags(null);
        when(paintingRepository.findWithDetailsById(14L)).thenReturn(Optional.of(painting));
        when(tagRepository.findByName("New")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paintingRepository.save(painting)).thenReturn(painting);

        PaintingDto result = paintingService.addTagToPainting(14L, "New");

        assertThat(result.tagNames()).containsExactly("New");
    }

    @Test
    void addTagToPaintingThrowsWhenPaintingMissing() {
        when(paintingRepository.findWithDetailsById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paintingService.addTagToPainting(15L, "Tag"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found: 15");
    }

    @Test
    void addTagToPaintingRequiresTagName() {
        Painting painting = painting(16L, "A", "B");
        when(paintingRepository.findWithDetailsById(16L)).thenReturn(Optional.of(painting));

        assertThatThrownBy(() -> paintingService.addTagToPainting(16L, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tag name is required");

        assertThatThrownBy(() -> paintingService.addTagToPainting(16L, " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tag name is required");
    }

    @Test
    void removeTagFromPaintingRemovesMatchingTagIgnoringCase() {
        Painting painting = painting(17L, "A", "B");
        painting.setTags(new HashSet<>(Set.of(tag("Modern"), tag("Classic"))));
        when(paintingRepository.findWithDetailsById(17L)).thenReturn(Optional.of(painting));
        when(paintingRepository.save(painting)).thenReturn(painting);

        PaintingDto result = paintingService.removeTagFromPainting(17L, " modern ");

        assertThat(result.tagNames()).containsExactly("Classic");
    }

    @Test
    void removeTagFromPaintingAllowsNullTags() {
        Painting painting = painting(18L, "A", "B");
        painting.setTags(null);
        when(paintingRepository.findWithDetailsById(18L)).thenReturn(Optional.of(painting));
        when(paintingRepository.save(painting)).thenReturn(painting);

        PaintingDto result = paintingService.removeTagFromPainting(18L, "Tag");

        assertThat(result.tagNames()).isEmpty();
    }

    @Test
    void removeTagFromPaintingThrowsWhenPaintingMissing() {
        when(paintingRepository.findWithDetailsById(19L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paintingService.removeTagFromPainting(19L, "Tag"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found: 19");
    }

    @Test
    void deletePaintingDeletesWhenExists() {
        when(paintingRepository.existsById(20L)).thenReturn(true);

        paintingService.deletePainting(20L);

        verify(paintingRepository).deleteById(20L);
    }

    @Test
    void deletePaintingThrowsWhenMissing() {
        when(paintingRepository.existsById(21L)).thenReturn(false);

        assertThatThrownBy(() -> paintingService.deletePainting(21L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Painting not found: 21");
    }

    @Test
    void validatePaintingRequiresTitle() {
        assertThatThrownBy(() -> paintingService.createPainting(dto(null, "Artist", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");

        assertThatThrownBy(() -> paintingService.createPainting(dto(" ", "Artist", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
    }

    @Test
    void validatePaintingRejectsLongTitle() {
        String title = "x".repeat(256);

        assertThatThrownBy(() -> paintingService.createPainting(dto(title, "Artist", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title too long");
    }

    @Test
    void validatePaintingRequiresArtist() {
        assertThatThrownBy(() -> paintingService.createPainting(dto("Title", null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Artist is required");

        assertThatThrownBy(() -> paintingService.createPainting(dto("Title", " ", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Artist is required");
    }

    @Test
    void validatePaintingRejectsLongDescription() {
        PaintingDto dto = new PaintingDto(
            null, "Title", "x".repeat(1001), "Artist", 2024, 10.0,
            "image", "oil", null, Set.of()
        );

        assertThatThrownBy(() -> paintingService.createPainting(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description too long");
    }

    @Test
    void validatePaintingRejectsYearOutsideRange() {
        PaintingDto tooOld = new PaintingDto(
            null, "Title", null, "Artist", 999, 10.0, null, null, null, Set.of()
        );
        PaintingDto future = new PaintingDto(
            null, "Title", null, "Artist", 2027, 10.0, null, null, null, Set.of()
        );

        assertThatThrownBy(() -> paintingService.createPainting(tooOld))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Year must be between 1000 and 2026");
        assertThatThrownBy(() -> paintingService.createPainting(future))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Year must be between 1000 and 2026");
    }

    private static PaintingDto dto(String title, String artist, String galleryName) {
        return new PaintingDto(
            null, title, " Description ", artist, 2024, 10.0,
            "image", "oil", galleryName, Set.of()
        );
    }

    private static Painting painting(Long id, String title, String artist) {
        Painting painting = new Painting();
        painting.setId(id);
        painting.setTitle(title);
        painting.setDescription("Description");
        painting.setArtist(artist);
        painting.setYear(2024);
        painting.setPrice(10.0);
        painting.setImageUrl("image");
        painting.setTechnique("oil");
        return painting;
    }

    private static Gallery gallery(String name) {
        Gallery gallery = new Gallery();
        gallery.setName(name);
        return gallery;
    }

    private static Tag tag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }
}

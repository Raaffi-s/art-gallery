package com.gallery.catalog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.BulkPaintingIdsDto;
import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.dto.UserDto;
import com.gallery.catalog.service.ExhibitionService;
import com.gallery.catalog.service.GalleryService;
import com.gallery.catalog.service.PaintingService;
import com.gallery.catalog.service.TagService;
import com.gallery.catalog.service.TransactionDemoService;
import com.gallery.catalog.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ControllerCoverageTest {

    @Mock
    private GalleryService galleryService;

    @Mock
    private PaintingService paintingService;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @Mock
    private ExhibitionService exhibitionService;

    @Mock
    private TransactionDemoService transactionDemoService;

    @Test
    void galleryControllerDelegatesAllEndpoints() {
        GalleryController controller = new GalleryController(galleryService);
        GalleryDto dto = galleryDto();
        when(galleryService.getAllGalleries()).thenReturn(List.of(dto));
        when(galleryService.getGalleriesByOwner(7L)).thenReturn(List.of(dto));
        when(galleryService.getGalleryById(1L)).thenReturn(dto);
        when(galleryService.createGallery(dto)).thenReturn(dto);
        when(galleryService.updateGallery(1L, dto)).thenReturn(dto);

        assertThat(controller.getAllGalleries(null).getBody()).containsExactly(dto);
        assertThat(controller.getAllGalleries(7L).getBody()).containsExactly(dto);
        assertThat(controller.getGalleryById(1L).getBody()).isEqualTo(dto);
        assertThat(controller.createGallery(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.updateGallery(1L, dto).getBody()).isEqualTo(dto);
        assertThat(controller.deleteGallery(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(galleryService).deleteGallery(1L);
    }

    @Test
    void paintingControllerDelegatesAllEndpoints() {
        PaintingController controller = new PaintingController(paintingService);
        PaintingDto dto = paintingDto();
        Page<PaintingDto> page = new PageImpl<>(List.of(dto));
        when(paintingService.getAllPaintings()).thenReturn(List.of(dto));
        when(paintingService.getPaintingsByArtist("Artist")).thenReturn(List.of(dto));
        when(paintingService.getPaintingsByGalleryName("Gallery")).thenReturn(List.of(dto));
        when(paintingService.getPaintingsByGalleryNameNative("Gallery")).thenReturn(List.of(dto));
        when(paintingService.getPaintingsByGalleryNamePaged("Gallery", 1, 2)).thenReturn(page);
        when(paintingService.getPaintingsByGalleryNameCached("Gallery", 1, 2)).thenReturn(List.of(dto));
        when(paintingService.getPaintingById(1L)).thenReturn(dto);
        when(paintingService.createPainting(dto)).thenReturn(dto);
        when(paintingService.updatePainting(1L, dto)).thenReturn(dto);
        when(paintingService.addTagToPainting(1L, "Tag")).thenReturn(dto);
        when(paintingService.removeTagFromPainting(1L, "Tag")).thenReturn(dto);

        assertThat(controller.getPaintings(null).getBody()).containsExactly(dto);
        assertThat(controller.getPaintings(" ").getBody()).containsExactly(dto);
        assertThat(controller.getPaintings("Artist").getBody()).containsExactly(dto);
        assertThat(controller.getPaintingsByGalleryJpql("Gallery").getBody()).containsExactly(dto);
        assertThat(controller.getPaintingsByGalleryNative("Gallery").getBody()).containsExactly(dto);
        assertThat(controller.getPaintingsByGalleryPaged("Gallery", 1, 2).getBody()).isEqualTo(page);
        assertThat(controller.getPaintingsByGalleryCached("Gallery", 1, 2).getBody()).containsExactly(dto);
        assertThat(controller.getPaintingById(1L).getBody()).isEqualTo(dto);
        assertThat(controller.createPainting(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.updatePainting(1L, dto).getBody()).isEqualTo(dto);
        assertThat(controller.addTagToPainting(1L, "Tag").getBody()).isEqualTo(dto);
        assertThat(controller.removeTagFromPainting(1L, "Tag").getBody()).isEqualTo(dto);
        assertThat(controller.deletePainting(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(paintingService).deletePainting(1L);
    }

    @Test
    void userControllerDelegatesAllEndpoints() {
        UserController controller = new UserController(userService);
        UserDto dto = userDto();
        when(userService.getAllUsers()).thenReturn(List.of(dto));
        when(userService.getUserByUsername("alice")).thenReturn(dto);
        when(userService.getUserById(1L)).thenReturn(dto);
        when(userService.createUser(dto)).thenReturn(dto);
        when(userService.updateUser(1L, dto)).thenReturn(dto);

        assertThat(controller.getUsers(null).getBody()).containsExactly(dto);
        assertThat(controller.getUsers("").getBody()).containsExactly(dto);
        assertThat(controller.getUsers("alice").getBody()).containsExactly(dto);
        assertThat(controller.getUserById(1L).getBody()).isEqualTo(dto);
        assertThat(controller.createUser(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.updateUser(1L, dto).getBody()).isEqualTo(dto);
        assertThat(controller.deleteUser(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).deleteUser(1L);
    }

    @Test
    void tagControllerDelegatesAllEndpoints() {
        TagController controller = new TagController(tagService);
        TagDto dto = tagDto();
        when(tagService.getAllTags()).thenReturn(List.of(dto));
        when(tagService.getTagByName("modern")).thenReturn(dto);
        when(tagService.getTagById(1L)).thenReturn(dto);
        when(tagService.createTag(dto)).thenReturn(dto);
        when(tagService.updateTag(1L, dto)).thenReturn(dto);

        assertThat(controller.getAllTags(null).getBody()).containsExactly(dto);
        assertThat(controller.getAllTags("").getBody()).containsExactly(dto);
        assertThat(controller.getAllTags("modern").getBody()).containsExactly(dto);
        assertThat(controller.getTagById(1L).getBody()).isEqualTo(dto);
        assertThat(controller.createTag(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.updateTag(1L, dto).getBody()).isEqualTo(dto);
        assertThat(controller.deleteTag(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(tagService).deleteTag(1L);
    }

    @Test
    void exhibitionControllerDelegatesAllEndpoints() {
        ExhibitionController controller = new ExhibitionController(exhibitionService);
        ExhibitionDto dto = exhibitionDto();
        BulkPaintingIdsDto ids = new BulkPaintingIdsDto(List.of(1L, 2L));
        when(exhibitionService.getAllExhibitions()).thenReturn(List.of(dto));
        when(exhibitionService.getExhibitionById(1L)).thenReturn(dto);
        when(exhibitionService.createExhibition(dto)).thenReturn(dto);
        when(exhibitionService.updateExhibition(1L, dto)).thenReturn(dto);
        when(exhibitionService.addPaintingToExhibition(1L, 2L)).thenReturn(dto);
        when(exhibitionService.removePaintingFromExhibition(1L, 2L)).thenReturn(dto);
        when(exhibitionService.addPaintingsToExhibitionBulk(1L, ids)).thenReturn(dto);
        when(exhibitionService.addPaintingsToExhibitionBulkWithoutTransactional(1L, ids)).thenReturn(dto);

        assertThat(controller.getAllExhibitions().getBody()).containsExactly(dto);
        assertThat(controller.getExhibitionById(1L).getBody()).isEqualTo(dto);
        assertThat(controller.createExhibition(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.updateExhibition(1L, dto).getBody()).isEqualTo(dto);
        assertThat(controller.deleteExhibition(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(controller.addPainting(1L, 2L).getBody()).isEqualTo(dto);
        assertThat(controller.removePainting(1L, 2L).getBody()).isEqualTo(dto);
        assertThat(controller.addPaintingsBulk(1L, ids).getBody()).isEqualTo(dto);
        assertThat(controller.addPaintingsBulkWithoutTransaction(1L, ids).getBody()).isEqualTo(dto);
        verify(exhibitionService).deleteExhibition(1L);
    }

    @Test
    void demoControllerDelegatesAllEndpoints() {
        DemoController controller = new DemoController(transactionDemoService);
        GalleryDto dto = galleryDto();

        assertThat(controller.showNPlusOneProblem().getBody()).isEqualTo("N+1 problem demo endpoint");
        assertThat(controller.createGalleryWithoutTransaction(dto).getBody())
            .isEqualTo("Gallery creation without transaction completed");
        assertThat(controller.createGalleryWithTransaction(dto).getBody())
            .isEqualTo("Gallery creation with transaction completed");
        verify(transactionDemoService).createGalleryWithoutTransaction(dto);
        verify(transactionDemoService).createGalleryWithTransaction(dto);
    }

    private static GalleryDto galleryDto() {
        return new GalleryDto(1L, "Gallery", "Description", "owner", 2);
    }

    private static PaintingDto paintingDto() {
        return new PaintingDto(
            1L, "Painting", "Description", "Artist", 2024, 10.0,
            "image", "oil", "Gallery", Set.of("Tag")
        );
    }

    private static UserDto userDto() {
        return new UserDto(1L, "alice", "alice@example.com", "Alice", "avatar", "bio", 1);
    }

    private static TagDto tagDto() {
        return new TagDto(1L, "modern", "description");
    }

    private static ExhibitionDto exhibitionDto() {
        return new ExhibitionDto(
            1L, "Expo", "Description",
            LocalDateTime.of(2026, 5, 7, 10, 0),
            LocalDateTime.of(2026, 5, 8, 10, 0),
            Set.of("Painting"), 1
        );
    }
}

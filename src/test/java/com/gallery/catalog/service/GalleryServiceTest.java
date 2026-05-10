package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.GalleryRepository;
import com.gallery.catalog.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GalleryServiceTest {

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GalleryService galleryService;

    @Test
    void getAllGalleriesReturnsDtos() {
        Gallery gallery = gallery(1L, "Main", "Desc", user("owner"));
        gallery.setPaintings(List.of(new Painting("A", "Artist")));
        when(galleryRepository.findAll()).thenReturn(List.of(gallery));

        List<GalleryDto> result = galleryService.getAllGalleries();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).ownerName()).isEqualTo("owner");
        assertThat(result.get(0).paintingsCount()).isEqualTo(1);
    }

    @Test
    void getGalleryByIdReturnsDtoWithNullOwnerAndPaintingsCount() {
        Gallery gallery = gallery(2L, "No owner", null, null);
        gallery.setPaintings(null);
        when(galleryRepository.findWithDetailsById(2L)).thenReturn(Optional.of(gallery));

        GalleryDto result = galleryService.getGalleryById(2L);

        assertThat(result.ownerName()).isNull();
        assertThat(result.paintingsCount()).isNull();
    }

    @Test
    void getGalleryByIdThrowsWhenMissing() {
        when(galleryRepository.findWithDetailsById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> galleryService.getGalleryById(3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Gallery not found: 3");
    }

    @Test
    void getGalleriesByOwnerReturnsDtos() {
        when(galleryRepository.findByOwnerId(4L))
            .thenReturn(List.of(gallery(4L, "Owned", null, user("owner"))));

        List<GalleryDto> result = galleryService.getGalleriesByOwner(4L);

        assertThat(result).extracting(GalleryDto::name).containsExactly("Owned");
    }

    @Test
    void createGalleryTrimsFieldsAndSaves() {
        User owner = user("owner");
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(galleryRepository.save(org.mockito.ArgumentMatchers.any(Gallery.class)))
            .thenAnswer(invocation -> {
                Gallery gallery = invocation.getArgument(0);
                gallery.setId(5L);
                return gallery;
            });

        GalleryDto result = galleryService.createGallery(dto(" Gallery ", " Desc ", " owner "));

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.name()).isEqualTo("Gallery");
        assertThat(result.description()).isEqualTo("Desc");
    }

    @Test
    void createGalleryAllowsNullDescription() {
        User owner = user("owner");
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(galleryRepository.save(org.mockito.ArgumentMatchers.any(Gallery.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        GalleryDto result = galleryService.createGallery(dto("Gallery", null, "owner"));

        assertThat(result.description()).isNull();
    }

    @Test
    void createGalleryThrowsWhenOwnerMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> galleryService.createGallery(dto("Gallery", null, "missing")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: missing");
    }

    @Test
    void updateGalleryUpdatesOwnerWhenProvided() {
        Gallery existing = gallery(6L, "Old", "Old desc", user("old"));
        User newOwner = user("new");
        when(galleryRepository.findById(6L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("new")).thenReturn(Optional.of(newOwner));
        when(galleryRepository.save(existing)).thenReturn(existing);

        GalleryDto result = galleryService.updateGallery(6L, dto(" New ", " New desc ", " new "));

        assertThat(result.name()).isEqualTo("New");
        assertThat(result.ownerName()).isEqualTo("new");
    }

    @Test
    void updateGalleryKeepsOwnerWhenOwnerNameIsNullOrBlank() {
        Gallery existing = gallery(7L, "Old", "Old desc", user("old"));
        when(galleryRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(galleryRepository.save(existing)).thenReturn(existing);

        GalleryDto nullOwner = new GalleryDto(null, "Name", null, null, null);
        GalleryDto result = galleryService.updateGallery(7L, nullOwner);

        assertThat(result.ownerName()).isEqualTo("old");

        GalleryDto blankOwner = new GalleryDto(null, "Name", null, " ", null);
        GalleryDto blankResult = galleryService.updateGallery(7L, blankOwner);

        assertThat(blankResult.ownerName()).isEqualTo("old");
    }

    @Test
    void updateGalleryThrowsWhenGalleryMissing() {
        when(galleryRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> galleryService.updateGallery(8L, dto("Name", null, "owner")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Gallery not found: 8");
    }

    @Test
    void updateGalleryThrowsWhenNewOwnerMissing() {
        Gallery existing = gallery(9L, "Old", null, user("old"));
        when(galleryRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> galleryService.updateGallery(9L, dto("Name", null, "ghost")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: ghost");
    }

    @Test
    void deleteGalleryDeletesWhenExists() {
        when(galleryRepository.existsById(10L)).thenReturn(true);

        galleryService.deleteGallery(10L);

        verify(galleryRepository).deleteById(10L);
    }

    @Test
    void deleteGalleryThrowsWhenMissing() {
        when(galleryRepository.existsById(11L)).thenReturn(false);

        assertThatThrownBy(() -> galleryService.deleteGallery(11L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Gallery not found: 11");
    }

    @Test
    void createGalleryRequiresName() {
        assertThatThrownBy(() -> galleryService.createGallery(dto(null, null, "owner")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Gallery name is required");

        assertThatThrownBy(() -> galleryService.createGallery(dto(" ", null, "owner")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Gallery name is required");

        verifyNoInteractions(galleryRepository, userRepository);
    }

    @Test
    void createGalleryRequiresOwnerName() {
        assertThatThrownBy(() -> galleryService.createGallery(dto("Gallery", null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Owner name is required");

        assertThatThrownBy(() -> galleryService.createGallery(dto("Gallery", null, " ")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Owner name is required");

        verifyNoInteractions(galleryRepository, userRepository);
    }

    private static GalleryDto dto(String name, String description, String ownerName) {
        return new GalleryDto(null, name, description, ownerName, null);
    }

    private static User user(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    private static Gallery gallery(Long id, String name, String description, User owner) {
        Gallery gallery = new Gallery();
        gallery.setId(id);
        gallery.setName(name);
        gallery.setDescription(description);
        gallery.setOwner(owner);
        return gallery;
    }
}

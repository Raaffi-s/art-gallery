package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.exception.DemoTransactionException;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.GalleryRepository;
import com.gallery.catalog.repository.PaintingRepository;
import com.gallery.catalog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionDemoServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private PaintingRepository paintingRepository;

    @InjectMocks
    private TransactionDemoService transactionDemoService;

    @Test
    void createGalleryWithTransactionCreatesEntitiesThenThrows() {
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(galleryRepository.save(org.mockito.ArgumentMatchers.any(Gallery.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(paintingRepository.save(org.mockito.ArgumentMatchers.any(Painting.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        GalleryDto dto = new GalleryDto(null, " Demo ", " Desc ", " Owner ", null);

        assertThatThrownBy(() -> transactionDemoService.createGalleryWithTransaction(dto))
            .isInstanceOf(DemoTransactionException.class)
            .hasMessage("Simulated error with transaction");

        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
            user.getUsername().startsWith("Owner_")
                && user.getEmail().startsWith("Owner")
                && user.getEmail().endsWith("@example.com")
                && user.getFullName().equals("Owner")
        ));
        verify(galleryRepository).save(org.mockito.ArgumentMatchers.argThat(gallery ->
            gallery.getName().equals("Demo")
                && gallery.getDescription().equals("Desc")
                && gallery.getOwner() != null
        ));
        verify(paintingRepository).save(org.mockito.ArgumentMatchers.argThat(painting ->
            painting.getTitle().startsWith("TX Demo Painting ")
                && painting.getArtist().equals("Owner")
                && painting.getYear().equals(2024)
                && painting.getPrice().equals(1000.0)
        ));
    }

    @Test
    void createGalleryWithoutTransactionUsesDefaultsForNullValuesThenThrows() {
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(galleryRepository.save(org.mockito.ArgumentMatchers.any(Gallery.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(paintingRepository.save(org.mockito.ArgumentMatchers.any(Painting.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        GalleryDto dto = new GalleryDto(null, null, null, null, null);

        assertThatThrownBy(() -> transactionDemoService.createGalleryWithoutTransaction(dto))
            .isInstanceOf(DemoTransactionException.class)
            .hasMessage("Simulated error without transaction");

        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
            user.getUsername().startsWith("demo_owner_")
                && user.getFullName().equals("demo_owner")
        ));
        verify(galleryRepository).save(org.mockito.ArgumentMatchers.argThat(gallery ->
            gallery.getName().startsWith("Demo Gallery ")
                && gallery.getDescription() == null
        ));
    }

    @Test
    void createGalleryWithoutTransactionUsesDefaultsForBlankValuesThenThrows() {
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(galleryRepository.save(org.mockito.ArgumentMatchers.any(Gallery.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(paintingRepository.save(org.mockito.ArgumentMatchers.any(Painting.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        GalleryDto dto = new GalleryDto(null, " ", " Desc ", " ", null);

        assertThatThrownBy(() -> transactionDemoService.createGalleryWithoutTransaction(dto))
            .isInstanceOf(DemoTransactionException.class);

        verify(galleryRepository).save(org.mockito.ArgumentMatchers.argThat(gallery -> {
            assertThat(gallery.getDescription()).isEqualTo("Desc");
            return gallery.getName().startsWith("Demo Gallery ");
        }));
    }
}

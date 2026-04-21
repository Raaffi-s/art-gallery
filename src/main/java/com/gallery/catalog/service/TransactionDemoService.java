package com.gallery.catalog.service;

import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.GalleryRepository;
import com.gallery.catalog.repository.PaintingRepository;
import com.gallery.catalog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionDemoService {

    private static final String ARTIST_EMAIL_DOMAIN = "@example.com";

    private final UserRepository userRepository;
    private final GalleryRepository galleryRepository;
    private final PaintingRepository paintingRepository;

    public TransactionDemoService(
        UserRepository userRepository,
        GalleryRepository galleryRepository,
        PaintingRepository paintingRepository
    ) {
        this.userRepository = userRepository;
        this.galleryRepository = galleryRepository;
        this.paintingRepository = paintingRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createGalleryWithTransaction(GalleryDto dto) {
        User user = createUser(dto);
        Gallery gallery = createGallery(dto, user);
        createPainting(gallery, user.getFullName());

        throw new RuntimeException("Simulated error with transaction");
    }

    public void createGalleryWithoutTransaction(GalleryDto dto) {
        User user = createUser(dto);
        Gallery gallery = createGallery(dto, user);
        createPainting(gallery, user.getFullName());

        throw new RuntimeException("Simulated error without transaction");
    }

    private User createUser(GalleryDto dto) {
        long suffix = System.currentTimeMillis();
        String ownerName = dto.ownerName() != null && !dto.ownerName().isBlank()
            ? dto.ownerName().trim()
            : "demo_owner";

        User user = new User();
        user.setUsername(ownerName + "_" + suffix);
        user.setEmail(ownerName + suffix + ARTIST_EMAIL_DOMAIN);
        user.setFullName(ownerName);

        return userRepository.save(user);
    }

    private Gallery createGallery(GalleryDto dto, User user) {
        Gallery gallery = new Gallery();
        gallery.setName(dto.name() != null && !dto.name().isBlank()
            ? dto.name().trim()
            : "Demo Gallery " + System.currentTimeMillis());
        gallery.setDescription(dto.description() != null ? dto.description().trim() : null);
        gallery.setOwner(user);

        return galleryRepository.save(gallery);
    }

    private Painting createPainting(Gallery gallery, String artistName) {
        Painting painting = new Painting();
        painting.setTitle("TX Demo Painting " + System.currentTimeMillis());
        painting.setArtist(artistName);
        painting.setDescription("Created for transaction demo");
        painting.setYear(2024);
        painting.setPrice(1000.0);
        painting.setGallery(gallery);

        return paintingRepository.save(painting);
    }
}
package com.gallery.catalog.service;

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

    private static final String ARTIST_USERNAME_PREFIX = "artist_";
    private static final String ARTIST_EMAIL_DOMAIN = "@example.com";
    private static final String GALLERY_NAME_PREFIX = "My Gallery ";
    private static final String PAINTING_TITLE_PREFIX = "Masterpiece ";

    private final UserRepository userRepository;
    private final GalleryRepository galleryRepository;
    private final PaintingRepository paintingRepository;

    public TransactionDemoService(
        UserRepository userRepository,
        GalleryRepository galleryRepository,
        PaintingRepository paintingRepository) {
        this.userRepository = userRepository;
        this.galleryRepository = galleryRepository;
        this.paintingRepository = paintingRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createGalleryWithTransaction() {
        User user = createUser();
        Gallery gallery = createGallery(user);
        createPainting(user, gallery);

        throw new RuntimeException("Simulated error - transaction rolling back all data");
    }

    public void createGalleryWithoutTransaction() {
        User user = createUser();
        Gallery gallery = createGallery(user);
        createPainting(user, gallery);

        throw new RuntimeException("Simulated error - data already saved in database!");
    }

    private User createUser() {
        User user = new User();
        user.setUsername(ARTIST_USERNAME_PREFIX + System.currentTimeMillis());
        user.setEmail(ARTIST_USERNAME_PREFIX + System.currentTimeMillis() + ARTIST_EMAIL_DOMAIN);
        user.setFullName("Test Artist");
        return userRepository.save(user);
    }

    private Gallery createGallery(User user) {
        Gallery gallery = new Gallery();
        gallery.setName(GALLERY_NAME_PREFIX + System.currentTimeMillis());
        gallery.setDescription("Test Gallery Description");
        gallery.setOwner(user);
        return galleryRepository.save(gallery);
    }

    private Painting createPainting(User user, Gallery gallery) {
        Painting painting = new Painting();
        painting.setTitle(PAINTING_TITLE_PREFIX + System.currentTimeMillis());
        painting.setArtist(user.getFullName());
        painting.setYear(2024);
        painting.setPrice(1_000_000.0);
        painting.setUser(user);
        painting.setGallery(gallery);
        return paintingRepository.save(painting);
    }
}
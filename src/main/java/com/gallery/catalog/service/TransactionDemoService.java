package com.gallery.catalog.service;

import com.gallery.catalog.dto.TransactionRequest;
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

    public void createGalleryWithoutTransaction(TransactionRequest request) {
        TransactionRequest effective = prepareRequest(request);

        User user = createUser(effective);
        Gallery gallery = createGallery(user, effective);
        createPainting(gallery, effective);

        throw new RuntimeException("Simulated error - data already saved in database!");
    }

    @Transactional(rollbackFor = Exception.class)
    public void createGalleryWithTransaction(TransactionRequest request) {
        TransactionRequest effective = prepareRequest(request);

        User user = createUser(effective);
        Gallery gallery = createGallery(user, effective);
        createPainting(gallery, effective);

        throw new RuntimeException("Simulated error - transaction rolling back all data");
    }

    private TransactionRequest prepareRequest(TransactionRequest request) {
        if (request == null) {
            return new TransactionRequest(
                "Test Artist",
                null,
                null,
                1_000_000L,
                2024
            );
        }

        String fullName = request.userFullName() != null
            ? request.userFullName()
            : "Test Artist";
        String galleryName = request.galleryName();
        String paintingTitle = request.paintingTitle();
        Long price = request.price() != null ? request.price() : 1_000_000L;
        Integer year = request.year() != null ? request.year() : 2024;

        return new TransactionRequest(fullName, galleryName, paintingTitle, price, year);
    }

    private User createUser(TransactionRequest request) {
        User user = new User();
        user.setUsername(ARTIST_USERNAME_PREFIX + System.currentTimeMillis());
        user.setEmail(ARTIST_USERNAME_PREFIX + System.currentTimeMillis() + ARTIST_EMAIL_DOMAIN);
        user.setFullName(request.userFullName());
        return userRepository.save(user);
    }

    private Gallery createGallery(User user, TransactionRequest request) {
        Gallery gallery = new Gallery();
        String name = request.galleryName() != null
            ? request.galleryName()
            : GALLERY_NAME_PREFIX + System.currentTimeMillis();
        gallery.setName(name);
        gallery.setDescription("Test Gallery Description");
        gallery.setOwner(user);
        return galleryRepository.save(gallery);
    }

    private void createPainting(Gallery gallery, TransactionRequest request) {
        Painting painting = new Painting();
        String title = request.paintingTitle() != null
            ? request.paintingTitle()
            : PAINTING_TITLE_PREFIX + System.currentTimeMillis();
        painting.setTitle(title);
        painting.setArtist(gallery.getOwner().getFullName());
        painting.setYear(request.year());
        painting.setPrice(request.price());
        painting.setGallery(gallery);
        paintingRepository.save(painting);
    }
}
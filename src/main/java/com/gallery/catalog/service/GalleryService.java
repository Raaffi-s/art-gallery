package com.gallery.catalog.service;

import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.GalleryRepository;
import com.gallery.catalog.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;

    public GalleryService(GalleryRepository galleryRepository,
                          UserRepository userRepository) {
        this.galleryRepository = galleryRepository;
        this.userRepository = userRepository;
    }

    private GalleryDto convertToDto(Gallery gallery) {
        GalleryDto dto = new GalleryDto();
        dto.setId(gallery.getId());
        dto.setName(gallery.getName());
        dto.setDescription(gallery.getDescription());

        if (gallery.getOwner() != null) {
            dto.setOwnerName(gallery.getOwner().getUsername());
        }

        if (gallery.getPaintings() != null) {
            dto.setPaintingsCount(gallery.getPaintings().size());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<GalleryDto> getAllGalleries() {
        return galleryRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public GalleryDto getGalleryById(Long id) {
        Gallery gallery = galleryRepository.findWithDetailsById(id)
            .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + id));
        return convertToDto(gallery);
    }

    @Transactional(readOnly = true)
    public List<GalleryDto> getGalleriesByOwner(Long ownerId) {
        return galleryRepository.findByOwnerId(ownerId)
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional
    public GalleryDto createGallery(GalleryDto dto) {
        validateGalleryDto(dto);

        User owner = userRepository.findByUsername(dto.getOwnerName().trim())
            .orElseThrow(() -> new IllegalArgumentException(
                "User not found: " + dto.getOwnerName()));

        Gallery gallery = new Gallery();
        gallery.setName(dto.getName().trim());
        gallery.setDescription(
            dto.getDescription() != null ? dto.getDescription().trim() : null);
        gallery.setOwner(owner);

        Gallery saved = galleryRepository.save(gallery);
        return convertToDto(saved);
    }

    @Transactional
    public GalleryDto updateGallery(Long id, GalleryDto dto) {
        validateGalleryDto(dto);

        Gallery gallery = galleryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + id));

        gallery.setName(dto.getName().trim());
        gallery.setDescription(
            dto.getDescription() != null ? dto.getDescription().trim() : null);

        if (dto.getOwnerName() != null && !dto.getOwnerName().trim().isEmpty()) {
            User owner = userRepository.findByUsername(dto.getOwnerName().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                    "User not found: " + dto.getOwnerName()));
            gallery.setOwner(owner);
        }

        Gallery updated = galleryRepository.save(gallery);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteGallery(Long id) {
        if (!galleryRepository.existsById(id)) {
            throw new IllegalArgumentException("Gallery not found: " + id);
        }
        galleryRepository.deleteById(id);
    }

    private void validateGalleryDto(GalleryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Gallery name is required");
        }
        if (dto.getOwnerName() == null || dto.getOwnerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name is required");
        }
    }
}


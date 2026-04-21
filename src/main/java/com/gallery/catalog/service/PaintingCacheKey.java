package com.gallery.catalog.service;

import java.util.Objects;

public record PaintingCacheKey(String galleryName, int page, int size) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaintingCacheKey other)) return false;
        return page == other.page
            && size == other.size
            && galleryName.equalsIgnoreCase(other.galleryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(galleryName.toLowerCase(), page, size);
    }
}
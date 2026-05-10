package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaintingCacheKeyTest {

    @Test
    void equalsReturnsTrueForSameInstance() {
        PaintingCacheKey key = new PaintingCacheKey("Modern", 0, 10);

        assertThat(key).isEqualTo(key);
    }

    @Test
    void equalsReturnsTrueIgnoringGalleryNameCase() {
        PaintingCacheKey left = new PaintingCacheKey("Modern", 0, 10);
        PaintingCacheKey right = new PaintingCacheKey("modern", 0, 10);

        assertThat(left).isEqualTo(right);
        assertThat(left.hashCode()).isEqualTo(right.hashCode());
    }

    @Test
    void equalsReturnsFalseForDifferentPage() {
        PaintingCacheKey left = new PaintingCacheKey("Modern", 0, 10);
        PaintingCacheKey right = new PaintingCacheKey("Modern", 1, 10);

        assertThat(left).isNotEqualTo(right);
    }

    @Test
    void equalsReturnsFalseForDifferentSize() {
        PaintingCacheKey left = new PaintingCacheKey("Modern", 0, 10);
        PaintingCacheKey right = new PaintingCacheKey("Modern", 0, 20);

        assertThat(left).isNotEqualTo(right);
    }

    @Test
    void equalsReturnsFalseForDifferentGalleryName() {
        PaintingCacheKey left = new PaintingCacheKey("Modern", 0, 10);
        PaintingCacheKey right = new PaintingCacheKey("Classic", 0, 10);

        assertThat(left).isNotEqualTo(right);
    }

    @Test
    void equalsReturnsFalseForNullAndOtherType() {
        PaintingCacheKey key = new PaintingCacheKey("Modern", 0, 10);

        assertThat(key.equals(null)).isFalse();
        assertThat(key.equals("test")).isFalse();
    }
}
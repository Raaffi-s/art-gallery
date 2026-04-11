package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaintingDto extends BaseDto {

    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    private String title;
    private String description;
    private String artist;
    private Integer year;
    private Double price;
    private String imageUrl;
    private String technique;
    private String galleryName;
    private Set<String> tagNames;

    public PaintingDto() {
        //пустой конструктор
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title too long");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description too long");
        }
        this.description = description;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTechnique() {
        return technique;
    }

    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public String getGalleryName() {
        return galleryName;
    }

    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

    @Override
    public String toString() {
        return String.format(
            "PaintingDto{id=%d, title='%s', artist='%s', year=%d}",
            getId(), title, artist, year
        );
    }
}
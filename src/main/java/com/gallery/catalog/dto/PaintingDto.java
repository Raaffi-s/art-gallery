package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for transferring painting data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaintingDto {

    private Long id;
    private String title;
    private String description;
    private String artist;
    private Integer year;
    private Double price;
    private String imageUrl;
    private String technique;
    private String userName;
    private String galleryName;
    private Set<String> tagNames;

    /**
     * Default constructor.
     */
    public PaintingDto() {
    }

    /**
     * Constructor with all fields.
     */
    public PaintingDto(
        Long id,
        String title,
        String description,
        String artist,
        Integer year,
        Double price,
        String imageUrl,
        String technique,
        String userName,
        String galleryName,
        Set<String> tagNames) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.artist = artist;
        this.year = year;
        this.price = price;
        this.imageUrl = imageUrl;
        this.technique = technique;
        this.userName = userName;
        this.galleryName = galleryName;
        this.tagNames = tagNames;
    }

    // ============== Getters and Setters ==============

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    // ============== equals, hashCode, toString ==============

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PaintingDto that = (PaintingDto) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PaintingDto{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", artist='" + artist + '\''
            + '}';
    }
}
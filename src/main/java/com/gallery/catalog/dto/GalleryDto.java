package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

/**
 * DTO for transferring gallery data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GalleryDto {

    private Long id;
    private String name;
    private String description;
    private String ownerName;
    private Integer paintingsCount;

    /**
     * Default constructor.
     */
    public GalleryDto() {
    }

    /**
     * Constructor with all fields.
     */
    public GalleryDto(
        Long id,
        String name,
        String description,
        String ownerName,
        Integer paintingsCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerName = ownerName;
        this.paintingsCount = paintingsCount;
    }

    // ============== Getters and Setters ==============

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getPaintingsCount() {
        return paintingsCount;
    }

    public void setPaintingsCount(Integer paintingsCount) {
        this.paintingsCount = paintingsCount;
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
        GalleryDto that = (GalleryDto) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GalleryDto{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", owner='" + ownerName + '\''
            + '}';
    }
}
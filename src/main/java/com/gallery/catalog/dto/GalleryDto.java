package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GalleryDto extends BaseDto {

    private String name;
    private String description;
    private String ownerName;
    private Integer paintingsCount;

    public GalleryDto() {
    }

    // Геттеры и сеттеры
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

    @Override
    public String toString() {
        return "GalleryDto{"
            + "id=" + getId()
            + ", name='" + name + '\''
            + '}';
    }
}
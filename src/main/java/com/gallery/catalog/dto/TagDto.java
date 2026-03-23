package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagDto extends BaseDto {

    private String name;
    private String description;

    public TagDto() {
        // Default constructor required for JSON (de)serialization
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

    @Override
    public String toString() {
        return "TagDto{"
            + "id=" + getId()
            + ", name='" + name + '\''
            + '}';
    }
}
package com.gallery.catalog.dto;

public abstract class BaseDto {

    protected Long id;  // ИЗМЕНИЛИ С private НА protected

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
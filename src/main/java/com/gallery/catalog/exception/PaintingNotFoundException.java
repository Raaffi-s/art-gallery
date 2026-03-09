package com.gallery.catalog.exception;

public class PaintingNotFoundException extends RuntimeException {

    public PaintingNotFoundException(Long id) {
        super("Painting not found with id: " + id);
    }

    public PaintingNotFoundException(String message) {
        super(message);
    }
}
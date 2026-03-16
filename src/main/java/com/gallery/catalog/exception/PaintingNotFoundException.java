package com.gallery.catalog.exception;

public class PaintingNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "Painting not found with id: %d";
    private static final String ERROR_MESSAGE_CUSTOM = "Painting error: %s";

    public PaintingNotFoundException(Long id) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, id));
    }

    public PaintingNotFoundException(String message) {
        super(String.format(ERROR_MESSAGE_CUSTOM, message));
    }
}
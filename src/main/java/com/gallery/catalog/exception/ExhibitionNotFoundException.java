package com.gallery.catalog.exception;

public class ExhibitionNotFoundException extends RuntimeException {
    public ExhibitionNotFoundException(String message) {
        super(message);
    }
}
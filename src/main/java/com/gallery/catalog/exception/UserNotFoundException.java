package com.gallery.catalog.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}
package com.gallery.catalog.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE_USERNAME_TEMPLATE =
        "User not found with username: %s";
    private static final String ERROR_MESSAGE_ID_TEMPLATE = "User not found with id: %d";

    public UserNotFoundException(String username) {
        super(String.format(ERROR_MESSAGE_USERNAME_TEMPLATE, username));
    }

    public UserNotFoundException(Long id) {
        super(String.format(ERROR_MESSAGE_ID_TEMPLATE, id));
    }
}
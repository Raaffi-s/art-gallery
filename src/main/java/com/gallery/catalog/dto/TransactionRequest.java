package com.gallery.catalog.dto;

public record TransactionRequest(
    String userFullName,
    String galleryName,
    String paintingTitle,
    Double price,
    Integer year
) {
}
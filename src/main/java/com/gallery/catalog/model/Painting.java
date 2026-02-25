package com.gallery.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Painting {

    private Long id;
    private String title;
    private String artist;
    private Integer year;
    private Double price;
}
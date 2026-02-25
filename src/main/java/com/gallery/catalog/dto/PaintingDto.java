package com.gallery.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaintingDto {

    private Long id;
    private String title;
    private String artist;
    private Integer year;
    private Double price;
}
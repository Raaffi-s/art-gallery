package com.gallery.catalog.dto;

public class PaintingDto {
    private Long id;
    private String title;
    private String artist;
    private Integer year;
    private Double price;

    public PaintingDto() {}

    public PaintingDto(Long id, String title, String artist, Integer year, Double price) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
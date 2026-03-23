package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends BaseDto {

    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private Integer paintingsCount;
    private Integer galleriesCount;


    // Геттеры и сеттеры
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getPaintingsCount() {
        return paintingsCount;
    }

    public void setPaintingsCount(Integer paintingsCount) {
        this.paintingsCount = paintingsCount;
    }

    public Integer getGalleriesCount() {
        return galleriesCount;
    }

    public void setGalleriesCount(Integer galleriesCount) {
        this.galleriesCount = galleriesCount;
    }

    @Override
    public String toString() {
        return "UserDto{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email
            + '\'' + '}';
    }
}
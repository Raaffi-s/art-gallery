package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

/**
 * DTO for transferring user data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private Integer paintingsCount;
    private Integer galleriesCount;

    /**
     * Default constructor.
     */
    public UserDto() {
    }

    /**
     * Constructor with all fields.
     */
    public UserDto(
        Long id,
        String username,
        String email,
        String fullName,
        String avatarUrl,
        String bio,
        Integer paintingsCount,
        Integer galleriesCount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.paintingsCount = paintingsCount;
        this.galleriesCount = galleriesCount;
    }

    // ============== Getters and Setters ==============

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    // ============== equals, hashCode, toString ==============

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDto userDto = (UserDto) obj;
        return Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserDto{"
            + "id=" + id
            + ", username='" + username + '\''
            + ", email='" + email + '\''
            + '}';
    }
}
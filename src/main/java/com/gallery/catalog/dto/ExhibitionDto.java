package com.gallery.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Set;

@SuppressWarnings("java:S1192")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExhibitionDto extends BaseDto {

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<String> paintingTitles;
    private Integer paintingsCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Set<String> getPaintingTitles() {
        return paintingTitles;
    }

    public void setPaintingTitles(Set<String> paintingTitles) {
        this.paintingTitles = paintingTitles;
    }

    public Integer getPaintingsCount() {
        return paintingsCount;
    }

    public void setPaintingsCount(Integer paintingsCount) {
        this.paintingsCount = paintingsCount;
    }

    @Override
    public String toString() {
        return "ExhibitionDto{"
            + "id=" + getId()
            + ", title='" + title + '\''
            + ", startDate=" + startDate
            + ", endDate=" + endDate
            + '}';
    }
}

package com.gallery.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exhibitions")
public class Exhibition extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * FetchType.LAZY — выбран намеренно: при загрузке выставки нам не всегда нужен
     * весь список картин. Lazy позволяет избежать лишних JOIN-запросов.
     * Каскад не указан (по умолчанию нет), т.к. Exhibition не является владельцем
     * жизненного цикла картин — удаление выставки не должно удалять картины.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "exhibition_paintings",
        joinColumns = @JoinColumn(name = "exhibition_id"),
        inverseJoinColumns = @JoinColumn(name = "painting_id")
    )
    private Set<Painting> paintings = new HashSet<>();

    public Exhibition() {
    }

    public Exhibition(String title) {
        this.title = title;
    }


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

    public Set<Painting> getPaintings() {
        return paintings;
    }

    public void setPaintings(Set<Painting> paintings) {
        this.paintings = paintings;
    }

    @Override
    public String toString() {
        return "Exhibition{"
            + "id=" + getId()
            + ", title='" + title + '\''
            + ", startDate=" + startDate
            + ", endDate=" + endDate
            + '}';
    }
}

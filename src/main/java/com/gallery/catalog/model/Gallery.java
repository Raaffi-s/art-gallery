package com.gallery.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "galleries")
public class Gallery extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "demo_number", unique = true)
    private Long demoNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "gallery")
    private List<Painting> paintings = new ArrayList<>();

    public Gallery() {
    }

    public Gallery(String name) {
        this.name = name;
    }

    @Override
    @PrePersist
    public void onCreate() {
        setCreatedAt(LocalDateTime.now());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDemoNumber() {
        return demoNumber;
    }

    public void setDemoNumber(Long demoNumber) {
        this.demoNumber = demoNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Painting> getPaintings() {
        return paintings;
    }

    public void setPaintings(List<Painting> paintings) {
        this.paintings = paintings;
    }

    @Override
    public String toString() {
        return "Gallery{"
            + "id=" + getId()
            + ", name='" + name + '\''
            + ", demoNumber=" + demoNumber
            + '}';
    }
}
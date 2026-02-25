package com.gallery.catalog.repository;

import com.gallery.catalog.model.Painting;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaintingRepository {
    private final ConcurrentHashMap<Long, Painting> paintings = new ConcurrentHashMap<>();
    private Long nextId = 1L;

    public PaintingRepository() {
        // Добавляем тестовые данные
        save(new Painting(null, "Звездная ночь", "Ван Гог", 1889, 1000000.0));
        save(new Painting(null, "Мона Лиза", "Леонардо да Винчи", 1503, 8700000.0));
        save(new Painting(null, "Крик", "Мунк", 1893, 1200000.0));
        save(new Painting(null, "Подсолнухи", "Ван Гог", 1888, 8500000.0));
        save(new Painting(null, "Тайная вечеря", "Леонардо да Винчи", 1498, 9500000.0));
    }

    public List<Painting> findAll() {
        return new ArrayList<>(paintings.values());
    }

    public Painting findById(Long id) {
        return paintings.get(id);
    }

    public List<Painting> findByArtist(String artist) {
        List<Painting> result = new ArrayList<>();
        for (Painting p : paintings.values()) {
            if (p.getArtist().toLowerCase().contains(artist.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }

    public Painting save(Painting painting) {
        if (painting.getId() == null) {
            painting.setId(nextId++);
        }
        paintings.put(painting.getId(), painting);
        return painting;
    }
}
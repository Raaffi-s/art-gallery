package com.gallery.catalog.service;

import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {
    private static final String TAG_NOT_FOUND_MESSAGE = "Tag not found";
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(TAG_NOT_FOUND_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name.trim())
            .orElseThrow(() -> new IllegalArgumentException(TAG_NOT_FOUND_MESSAGE + name));
    }

    @Transactional
    public Tag createTag(Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        tagRepository.findByName(tag.getName().trim()).ifPresent(existing -> {
            throw new IllegalArgumentException(
                "Tag already exists: " + tag.getName());
        });

        tag.setName(tag.getName().trim());
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(Long id, Tag updated) {
        if (updated.getName() == null || updated.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));

        tag.setName(updated.getName().trim());
        tag.setDescription(updated.getDescription());
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found: " + id);
        }
        tagRepository.deleteById(id);
    }
}


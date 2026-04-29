package com.gallery.catalog.service;

import com.gallery.catalog.exception.TagNotFoundException;
import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    private TagDto convertToDto(Tag tag) {
        return new TagDto(
            tag.getId(),
            tag.getName(),
            tag.getDescription()
        );
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public TagDto getTagById(Long id) {
        return convertToDto(tagRepository.findById(id)
            .orElseThrow(() -> new TagNotFoundException(id.toString())));
    }

    @Transactional(readOnly = true)
    public TagDto getTagByName(String name) {
        return convertToDto(tagRepository.findByName(name.trim())
            .orElseThrow(() -> new TagNotFoundException(name)));
    }

    @Transactional
    public TagDto createTag(TagDto dto) {
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        tagRepository.findByName(dto.name().trim()).ifPresent(existing -> {
            throw new IllegalArgumentException("Tag already exists: " + dto.name());
        });

        Tag tag = new Tag();
        tag.setName(dto.name().trim());
        tag.setDescription(dto.description());
        return convertToDto(tagRepository.save(tag));
    }

    @Transactional
    public TagDto updateTag(Long id, TagDto dto) {
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }

        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new TagNotFoundException(id.toString()));

        tag.setName(dto.name().trim());
        tag.setDescription(dto.description());
        return convertToDto(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(id.toString());
        }
        tagRepository.deleteById(id);
    }
}
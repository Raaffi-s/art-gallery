package com.gallery.catalog.service;

import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.exception.TagNotFoundException;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + id));
        return convertToDto(tag);
    }

    @Transactional(readOnly = true)
    public TagDto getTagByName(String name) {
        Tag tag = tagRepository.findByName(name)
            .orElseThrow(() -> new TagNotFoundException("Tag not found with name: " + name));
        return convertToDto(tag);
    }

    @Transactional
    public TagDto createTag(TagDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.name());
        tag.setDescription(dto.description());
        Tag saved = tagRepository.save(tag);
        return convertToDto(saved);
    }

    @Transactional
    public TagDto updateTag(Long id, TagDto dto) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + id));
        tag.setName(dto.name());
        tag.setDescription(dto.description());
        Tag updated = tagRepository.save(tag);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }

    private TagDto convertToDto(Tag tag) {
        return new TagDto(tag.getId(), tag.getName(), tag.getDescription());
    }
}
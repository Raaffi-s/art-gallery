package com.gallery.catalog.service;

import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.exception.TagNotFoundException;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private static final String TAG_NOT_FOUND_WITH_ID = "Tag not found with id: ";
    private static final String TAG_NOT_FOUND_WITH_NAME = "Tag not found with name: ";

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

    private void updateTagFromDto(Tag tag, TagDto dto) {
        tag.setName(dto.name().trim());
        tag.setDescription(dto.description() != null ? dto.description().trim() : null);
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public TagDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new TagNotFoundException(TAG_NOT_FOUND_WITH_ID + id));
        return convertToDto(tag);
    }

    @Transactional(readOnly = true)
    public TagDto getTagByName(String name) {
        Tag tag = tagRepository.findAll().stream()
            .filter(t -> t.getName() != null && t.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new TagNotFoundException(TAG_NOT_FOUND_WITH_NAME + name));
        return convertToDto(tag);
    }

    @Transactional
    public TagDto createTag(TagDto dto) {
        validateTagDto(dto);

        Tag tag = new Tag();
        updateTagFromDto(tag, dto);

        return convertToDto(tagRepository.save(tag));
    }

    @Transactional
    public TagDto updateTag(Long id, TagDto dto) {
        validateTagDto(dto);

        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new TagNotFoundException(TAG_NOT_FOUND_WITH_ID + id));

        updateTagFromDto(tag, dto);

        return convertToDto(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(TAG_NOT_FOUND_WITH_ID + id);
        }
        tagRepository.deleteById(id);
    }

    private void validateTagDto(TagDto dto) {
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required");
        }
    }
}
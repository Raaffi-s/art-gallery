package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.exception.TagNotFoundException;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void getAllTagsReturnsDtos() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Oil");
        tag.setDescription("desc");

        when(tagRepository.findAll()).thenReturn(List.of(tag));

        List<TagDto> result = tagService.getAllTags();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Oil");
        assertThat(result.get(0).description()).isEqualTo("desc");
    }

    @Test
    void getAllTagsReturnsEmptyList() {
        when(tagRepository.findAll()).thenReturn(List.of());

        List<TagDto> result = tagService.getAllTags();

        assertThat(result).isEmpty();
    }

    @Test
    void getTagByIdReturnsDto() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Oil");
        tag.setDescription("desc");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        TagDto result = tagService.getTagById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Oil");
        assertThat(result.description()).isEqualTo("desc");
    }

    @Test
    void getTagByIdThrowsWhenMissing() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getTagById(1L))
            .isInstanceOf(TagNotFoundException.class)
            .hasMessage("Tag not found with id: 1");
    }

    @Test
    void getTagByNameFindsIgnoringCaseAndSkipsNullNames() {
        Tag nullName = new Tag();
        nullName.setId(1L);
        nullName.setName(null);

        Tag target = new Tag();
        target.setId(2L);
        target.setName("Oil");
        target.setDescription("desc");

        when(tagRepository.findAll()).thenReturn(List.of(nullName, target));

        TagDto result = tagService.getTagByName("oil");

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Oil");
        assertThat(result.description()).isEqualTo("desc");
    }

    @Test
    void getTagByNameThrowsWhenMissing() {
        when(tagRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> tagService.getTagByName("missing"))
            .isInstanceOf(TagNotFoundException.class)
            .hasMessage("Tag not found with name: missing");
    }

    @Test
    void createTagSavesTrimmedFields() {
        Tag saved = new Tag();
        saved.setId(1L);
        saved.setName("Oil");
        saved.setDescription("desc");

        when(tagRepository.save(any(Tag.class))).thenReturn(saved);

        TagDto dto = new TagDto(null, "  Oil  ", "  desc  ");

        TagDto result = tagService.createTag(dto);

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(captor.capture());

        assertThat(captor.getValue().getName()).isEqualTo("Oil");
        assertThat(captor.getValue().getDescription()).isEqualTo("desc");
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Oil");
    }

    @Test
    void createTagAllowsNullDescription() {
        Tag saved = new Tag();
        saved.setId(1L);
        saved.setName("Oil");
        saved.setDescription(null);

        when(tagRepository.save(any(Tag.class))).thenReturn(saved);

        TagDto result = tagService.createTag(new TagDto(null, "Oil", null));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Oil");
        assertThat(result.description()).isNull();
    }

    @Test
    void createTagAcceptsNameWithOuterSpacesAfterTrim() {
        Tag saved = new Tag();
        saved.setId(2L);
        saved.setName("Landscape");
        saved.setDescription("nature");

        when(tagRepository.save(any(Tag.class))).thenReturn(saved);

        TagDto result = tagService.createTag(new TagDto(null, "  Landscape  ", "nature"));

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Landscape");
    }

    @Test
    void createTagThrowsWhenNameMissing() {
        TagDto dto = new TagDto(null, "   ", "desc");

        assertThatThrownBy(() -> tagService.createTag(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tag name is required");
    }

    @Test
    void createTagThrowsWhenNameIsNull() {
        TagDto dto = new TagDto(null, null, "desc");

        assertThatThrownBy(() -> tagService.createTag(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tag name is required");
    }

    @Test
    void updateTagUpdatesExistingTag() {
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("Old");
        existing.setDescription("old");

        Tag saved = new Tag();
        saved.setId(1L);
        saved.setName("New");
        saved.setDescription("new desc");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(saved);

        TagDto result = tagService.updateTag(1L, new TagDto(null, "  New  ", "  new desc  "));

        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("new desc");
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New");
        assertThat(result.description()).isEqualTo("new desc");
    }

    @Test
    void updateTagSetsNullDescription() {
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("Old");
        existing.setDescription("old");

        Tag saved = new Tag();
        saved.setId(1L);
        saved.setName("New");
        saved.setDescription(null);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(saved);

        TagDto result = tagService.updateTag(1L, new TagDto(null, " New ", null));

        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isNull();
        assertThat(result.description()).isNull();
    }

    @Test
    void updateTagTrimsDescriptionToEmptyStringWhenBlankDescriptionPassed() {
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("Old");
        existing.setDescription("old");

        Tag saved = new Tag();
        saved.setId(1L);
        saved.setName("New");
        saved.setDescription("");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(saved);

        TagDto result = tagService.updateTag(1L, new TagDto(null, " New ", "   "));

        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("");
        assertThat(result.description()).isEqualTo("");
    }

    @Test
    void updateTagThrowsWhenMissing() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.updateTag(1L, new TagDto(null, "Oil", "desc")))
            .isInstanceOf(TagNotFoundException.class)
            .hasMessage("Tag not found with id: 1");
    }

    @Test
    void deleteTagDeletesWhenExists() {
        when(tagRepository.existsById(1L)).thenReturn(true);

        tagService.deleteTag(1L);

        verify(tagRepository).deleteById(1L);
    }

    @Test
    void deleteTagThrowsWhenMissing() {
        when(tagRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> tagService.deleteTag(1L))
            .isInstanceOf(TagNotFoundException.class)
            .hasMessage("Tag not found with id: 1");
    }
}
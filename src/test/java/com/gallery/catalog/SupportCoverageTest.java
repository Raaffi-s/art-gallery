package com.gallery.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gallery.catalog.aspect.LoggingAspect;
import com.gallery.catalog.dto.BaseDto;
import com.gallery.catalog.dto.BulkPaintingIdsDto;
import com.gallery.catalog.dto.ExhibitionDto;
import com.gallery.catalog.dto.GalleryDto;
import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.dto.TagDto;
import com.gallery.catalog.dto.TransactionRequest;
import com.gallery.catalog.dto.UserDto;
import com.gallery.catalog.logging.RequestLoggingFilter;
import com.gallery.catalog.model.Exhibition;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.Painting;
import com.gallery.catalog.model.Tag;
import com.gallery.catalog.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

class SupportCoverageTest {

    @Test
    void dtoRecordsAndBaseDtoExposeValues() {
        LocalDateTime now = LocalDateTime.of(2026, 5, 7, 10, 0);
        BaseDto dto = new TestBaseDto();
        dto.setId(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(new BulkPaintingIdsDto(List.of(1L)).paintingIds()).containsExactly(1L);
        assertThat(new ExhibitionDto(1L, "e", "d", now, now, Set.of("p"), 1).toString())
            .contains("ExhibitionDto");
        assertThat(new GalleryDto(1L, "g", "d", "o", 1).ownerName()).isEqualTo("o");
        assertThat(new PaintingDto(1L, "p", "d", "a", 2024, 1.0, "i", "t", "g", Set.of("tag")).tagNames())
            .containsExactly("tag");
        assertThat(new TagDto(1L, "tag", "d").description()).isEqualTo("d");
        assertThat(new TransactionRequest("u", "g", "p", 1.0, 2024).paintingTitle()).isEqualTo("p");
        assertThat(new UserDto(1L, "u", "e", "f", "a", "b", 1).galleriesCount()).isEqualTo(1);
    }

    @Test
    void modelAccessorsLifecycleToStringAndEqualityAreCovered() {
        User user = new User("user", "user@example.com");
        user.setId(1L);
        user.setFullName("User");
        user.setAvatarUrl("avatar");
        user.setBio("bio");
        user.setGalleries(new ArrayList<>());
        user.onCreate();

        Gallery gallery = new Gallery("Gallery");
        gallery.setId(2L);
        gallery.setDescription("description");
        gallery.setDemoNumber(3L);
        gallery.setOwner(user);
        gallery.setPaintings(new ArrayList<>());
        gallery.onCreate();

        Painting painting = new Painting("Painting", "Artist");
        painting.setId(4L);
        painting.setDescription("description");
        painting.setYear(2024);
        painting.setPrice(10.0);
        painting.setImageUrl("image");
        painting.setTechnique("oil");
        painting.setGallery(gallery);
        painting.setTags(new HashSet<>());
        painting.onCreate();

        Tag tag = new Tag("Tag");
        tag.setId(5L);
        tag.setDescription("description");
        tag.setPaintings(Set.of(painting));

        Exhibition exhibition = new Exhibition("Expo");
        exhibition.setId(6L);
        exhibition.setDescription("description");
        exhibition.setStartDate(LocalDateTime.of(2026, 5, 7, 10, 0));
        exhibition.setEndDate(LocalDateTime.of(2026, 5, 8, 10, 0));
        exhibition.setPaintings(Set.of(painting));
        exhibition.onCreate();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("user");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getFullName()).isEqualTo("User");
        assertThat(user.getAvatarUrl()).isEqualTo("avatar");
        assertThat(user.getBio()).isEqualTo("bio");
        assertThat(user.getGalleries()).isEmpty();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.toString()).contains("User");

        assertThat(gallery.getName()).isEqualTo("Gallery");
        assertThat(gallery.getDescription()).isEqualTo("description");
        assertThat(gallery.getDemoNumber()).isEqualTo(3L);
        assertThat(gallery.getOwner()).isEqualTo(user);
        assertThat(gallery.getPaintings()).isEmpty();
        assertThat(gallery.toString()).contains("Gallery");

        assertThat(painting.getTitle()).isEqualTo("Painting");
        assertThat(painting.getDescription()).isEqualTo("description");
        assertThat(painting.getArtist()).isEqualTo("Artist");
        assertThat(painting.getYear()).isEqualTo(2024);
        assertThat(painting.getPrice()).isEqualTo(10.0);
        assertThat(painting.getImageUrl()).isEqualTo("image");
        assertThat(painting.getTechnique()).isEqualTo("oil");
        assertThat(painting.getGallery()).isEqualTo(gallery);
        assertThat(painting.getTags()).isEmpty();

        assertThat(tag.getName()).isEqualTo("Tag");
        assertThat(tag.getDescription()).isEqualTo("description");
        assertThat(tag.getPaintings()).containsExactly(painting);
        assertThat(tag.toString()).contains("Tag");
        assertThat(tag).isEqualTo(tag);
        assertThat(tag).isEqualTo(new Tag("Tag"));
        assertThat(tag).isNotEqualTo(null);
        assertThat(tag).isNotEqualTo("Tag");
        assertThat(tag).isNotEqualTo(new Tag());
        assertThat(new Tag()).isEqualTo(new Tag());
        assertThat(new Tag()).isNotEqualTo(new Tag("Tag"));
        assertThat(tag.hashCode()).isEqualTo(new Tag("Tag").hashCode());
        assertThat(new Tag().hashCode()).isZero();

        assertThat(exhibition.getTitle()).isEqualTo("Expo");
        assertThat(exhibition.getDescription()).isEqualTo("description");
        assertThat(exhibition.getStartDate()).isEqualTo(LocalDateTime.of(2026, 5, 7, 10, 0));
        assertThat(exhibition.getEndDate()).isEqualTo(LocalDateTime.of(2026, 5, 8, 10, 0));
        assertThat(exhibition.getPaintings()).containsExactly(painting);
        assertThat(exhibition.toString()).contains("Exhibition");
    }

    @Test
    void requestLoggingFilterDelegatesAndLogsWithAndWithoutQueryString()
        throws ServletException, IOException {
        TestRequestLoggingFilter filter = new TestRequestLoggingFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(200);

        when(request.getQueryString()).thenReturn(null);
        filter.call(request, response, chain);

        when(request.getQueryString()).thenReturn("a=b");
        filter.call(request, response, chain);

        verify(chain, org.mockito.Mockito.times(2)).doFilter(request, response);
    }

    @Test
    void loggingAspectReturnsResultAndRethrowsFailures() throws Throwable {
        LoggingAspect aspect = new LoggingAspect();
        ProceedingJoinPoint success = joinPoint("service.success()");
        when(success.proceed()).thenReturn("ok");

        assertThat(aspect.logExecutionTime(success)).isEqualTo("ok");

        ProceedingJoinPoint failure = joinPoint("service.failure()");
        RuntimeException ex = new RuntimeException("boom");
        when(failure.proceed()).thenThrow(ex);

        assertThatThrownBy(() -> aspect.logExecutionTime(failure)).isSameAs(ex);
    }

    private static ProceedingJoinPoint joinPoint(String name) {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(name);
        return joinPoint;
    }

    private static class TestBaseDto extends BaseDto {
    }

    private static class TestRequestLoggingFilter extends RequestLoggingFilter {
        void call(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
            doFilterInternal(request, response, chain);
        }
    }
}

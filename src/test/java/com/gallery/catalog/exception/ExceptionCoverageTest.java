package com.gallery.catalog.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ExceptionCoverageTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void customExceptionsExposeMessages() {
        assertThat(new DemoTransactionException("demo")).hasMessage("demo");
        assertThat(new DuplicateResourceException("duplicate")).hasMessage("duplicate");
        assertThat(new ExhibitionNotFoundException("missing")).hasMessage("missing");
        assertThat(new PaintingNotFoundException(7L)).hasMessage("Painting not found with id: 7");
        assertThat(new PaintingNotFoundException("bad")).hasMessage("Painting error: bad");
        assertThat(new TagNotFoundException("tag")).hasMessage("tag");
        assertThat(new UserNotFoundException("alice")).hasMessage("User not found with username: alice");
        assertThat(new UserNotFoundException(8L)).hasMessage("User not found with id: 8");
    }

    @Test
    void handlersReturnExpectedStatuses() {
        HttpServletRequest request = request();

        assertThat(handler.handlePaintingNotFound(new PaintingNotFoundException(1L), request).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(handler.handleUserNotFound(new UserNotFoundException(1L), request).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(handler.handleTagNotFound(new TagNotFoundException("missing"), request).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(handler.handleExhibitionNotFound(new ExhibitionNotFoundException("missing"), request).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(handler.handleDuplicateResource(new DuplicateResourceException("duplicate"), request).getStatusCode())
            .isEqualTo(HttpStatus.CONFLICT);
        assertThat(handler.handleDataIntegrityViolation(new DataIntegrityViolationException("bad"), request).getBody().message())
            .isEqualTo("Data conflict: resource with such unique value already exists");
        assertThat(handler.handleIllegalArgument(new IllegalArgumentException("bad"), request).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(handler.handleAll(new RuntimeException("boom"), request).getStatusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void validationHandlerJoinsFieldErrors() {
        HttpServletRequest request = request();
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("dto", "name", "must not be blank"),
            new FieldError("dto", "email", "must be valid")
        ));

        ErrorResponse body = handler.handleValidation(ex, request).getBody();

        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("name: must not be blank; email: must be valid");
        assertThat(body.path()).isEqualTo("/api/test");
        assertThat(body.timestamp()).isNotNull();
    }

    private static HttpServletRequest request() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        return request;
    }
}

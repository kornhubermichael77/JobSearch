package info.kornhuber.jobsearch.config;

import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundReturns404StyleBody() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Map<String, Object> response = handler.handleNotFound(new NotFoundException("Company not found: 1"));

        assertEquals("Company not found: 1", response.get("error"));
    }

    @Test
    void handleJsonParseError_shouldReturnFriendlyMessage() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON parse error");

        Map<String, Object> result = handler.handleJsonParseError(ex);

        assertThat(result).containsEntry("error", "Ungültiger Enum-Wert im Request");
    }

    @Test
    void handleBadRequestReturnsErrorBody() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Map<String, Object> response = handler.handleBadRequest(new BadRequestException("Ungültiger Request"));

        assertEquals("Ungültiger Request", response.get("error"));
    }

    @Test
    void handleConflictReturnsErrorBody() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Map<String, Object> response = handler.handleConflict(new ConflictException("Email existiert bereits"));

        assertEquals("Email existiert bereits", response.get("error"));
    }
}
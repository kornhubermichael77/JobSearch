package info.kornhuber.jobsearch.config;

import info.kornhuber.jobsearch.dto.error.ApiErrorResponse;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    /**
     * Hilfsmethode für Tests:
     * Erzeugt ein Mock-Request-Objekt mit einer konkreten Request-URI.
     *
     * Warum?
     * Der neue GlobalExceptionHandler schreibt den Request-Pfad in die Error-Response.
     * Deshalb müssen wir im Test ein HttpServletRequest mitgeben.
     */
    private MockHttpServletRequest mockRequest(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    @Test
    void handleNotFound_shouldReturn404Response() {
        MockHttpServletRequest request = mockRequest("/api/companies/1");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Company not found: 1"),
                request
        );

        assertEquals(404, response.getStatusCode().value());

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertEquals("Not Found", body.error());
        assertEquals("Company not found: 1", body.message());
        assertEquals("/api/companies/1", body.path());
        assertThat(body.fieldErrors()).isEmpty();
    }

    @Test
    void handleJsonParseError_shouldReturnFriendlyMessage() {
        MockHttpServletRequest request = mockRequest("/api/companies");

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON parse error", (Throwable) null);

        ResponseEntity<ApiErrorResponse> response = handler.handleJsonParseError(ex, request);

        assertEquals(400, response.getStatusCode().value());

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertEquals("Bad Request", body.error());
        assertEquals("Malformed JSON or invalid enum value in request body", body.message());
        assertEquals("/api/companies", body.path());
        assertThat(body.fieldErrors()).isEmpty();
    }

    @Test
    void handleBadRequest_shouldReturn400Response() {
        MockHttpServletRequest request = mockRequest("/api/auth/login");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(
                new BadRequestException("Ungültiger Request"),
                request
        );

        assertEquals(400, response.getStatusCode().value());

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertEquals("Bad Request", body.error());
        assertEquals("Ungültiger Request", body.message());
        assertEquals("/api/auth/login", body.path());
        assertThat(body.fieldErrors()).isEmpty();
    }

    @Test
    void handleConflict_shouldReturn409Response() {
        MockHttpServletRequest request = mockRequest("/api/auth/register");

        ResponseEntity<ApiErrorResponse> response = handler.handleConflict(
                new ConflictException("Email existiert bereits"),
                request
        );

        assertEquals(409, response.getStatusCode().value());

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertEquals("Conflict", body.error());
        assertEquals("Email existiert bereits", body.message());
        assertEquals("/api/auth/register", body.path());
        assertThat(body.fieldErrors()).isEmpty();
    }
}
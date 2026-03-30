package info.kornhuber.jobsearch.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_shouldReturnErrorMessage() {
        RuntimeException ex = new RuntimeException("Communication not found: 99");

        Map<String, Object> result = handler.handleRuntimeException(ex);

        assertThat(result).containsEntry("error", "Communication not found: 99");
    }

    @Test
    void handleJsonParseError_shouldReturnFriendlyMessage() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON parse error");

        Map<String, Object> result = handler.handleJsonParseError(ex);

        assertThat(result).containsEntry("error", "Ungültiger Enum-Wert im Request");
    }
}
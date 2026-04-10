package info.kornhuber.jobsearch.dto.error;

import java.util.Map;

public record ApiFieldError(
        String field,
        String message,
        Object rejectedValue,
        Map<String, Object> constraints
) {
}
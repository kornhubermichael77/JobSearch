package info.kornhuber.jobsearch.config;

import info.kornhuber.jobsearch.dto.error.ApiErrorResponse;
import info.kornhuber.jobsearch.dto.error.ApiFieldError;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import info.kornhuber.jobsearch.multitenancy.TenantResolutionException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ApiFieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toApiFieldError)
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(TenantResolutionException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantResolutionException(
            TenantResolutionException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleJsonParseError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "Malformed JSON or invalid enum value in request body",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<ApiFieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(this::toApiFieldError)
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Request constraint validation failed",
                request.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Ungültige Zugangsdaten",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String message = "Invalid request parameter: " + ex.getName();

        ApiFieldError fieldError = new ApiFieldError(
                ex.getName(),
                message,
                ex.getValue(),
                ex.getRequiredType() != null && ex.getRequiredType().isEnum()
                        ? Map.of("allowedValues", ex.getRequiredType().getEnumConstants())
                        : Map.of()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "Request parameter validation failed",
                request.getRequestURI(),
                List.of(fieldError)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception for path {}", request.getRequestURI(), ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Interner Fehler",
                request.getRequestURI()
        );
    }

    private ApiFieldError toApiFieldError(FieldError error) {
        return new ApiFieldError(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue(),
                extractFieldConstraints(error)
        );
    }

    private ApiFieldError toApiFieldError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath() != null
                ? violation.getPropertyPath().toString()
                : "unknown";

        return new ApiFieldError(
                field,
                violation.getMessage(),
                violation.getInvalidValue(),
                violation.getConstraintDescriptor().getAttributes().entrySet().stream()
                        .filter(entry -> !List.of("message", "groups", "payload").contains(entry.getKey()))
                        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    private Map<String, Object> extractFieldConstraints(FieldError error) {
        Object[] arguments = error.getArguments();
        if (arguments == null) {
            return Map.of();
        }

        if ("Size".equals(error.getCode()) && arguments.length >= 3) {
            Object min = arguments[2];
            Object max = arguments[1];
            return Map.of(
                    "min", min,
                    "max", max
            );
        }

        if ("NotBlank".equals(error.getCode()) || "NotNull".equals(error.getCode())) {
            return Map.of("required", true);
        }

        if ("Email".equals(error.getCode())) {
            return Map.of("format", "email");
        }

        return Map.of();
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        return buildResponse(status, error, message, path, List.of());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message,
            String path,
            List<ApiFieldError> fieldErrors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                path,
                fieldErrors
        );

        return ResponseEntity.status(status).body(response);
    }
}
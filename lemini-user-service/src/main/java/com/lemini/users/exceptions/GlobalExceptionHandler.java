package com.lemini.users.exceptions;

import java.util.List;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.lemini.users.ui.model.response.ApiErrorResponse;

import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Handle custom user service exceptions
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleUserServiceException(
            UserServiceException ex,
            WebRequest request) {

        HttpStatus status = getHttpStatusFromErrorType(ex.getErrorType());
        String messageKey = Objects.requireNonNull(ex.getErrorType().getMessage())
            .replace("{", "")
            .replace("}", "");

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
            .message(messageSource.getMessage(
                    Objects.requireNonNull(messageKey),
                null,
                LocaleContextHolder.getLocale()))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED; // Default 401
        String errorMessage = messageSource.getMessage("auth.message.unauthorized", null, LocaleContextHolder.getLocale())
;
        // Logic to switch to 400 if it was a validation/service error
        if (ex instanceof AuthenticationServiceException) {
            status = HttpStatus.BAD_REQUEST; // Switch to 400
            errorMessage = messageSource.getMessage("auth.message.bad_request", null, LocaleContextHolder.getLocale());
        }

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(errorMessage)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(error);
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        Set<String> reportedFields = new HashSet<>();
        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .filter(error -> reportedFields.add(error.getField()))
            .map(error -> error.getField() + ": "
                + messageSource.getMessage(error, LocaleContextHolder.getLocale()))
            .toList();

        ApiErrorResponse error = ApiErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation failed")
            .path(request.getDescription(false).replace("uri=", ""))
            .details(validationErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler({ IllegalArgumentException.class, MissingPathVariableException.class,
            MethodArgumentTypeMismatchException.class, NoHandlerFoundException.class })
        public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(Exception ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Invalid or missing userId in path")
                .path("")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception ex, WebRequest request) {

        log.error("Unexpected server error", ex);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(messageSource.getMessage("user.err.unexpected", null, LocaleContextHolder.getLocale()))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private HttpStatus getHttpStatusFromErrorType(UserServiceException.UserErrorType type) {
        return switch (type) {
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN -> HttpStatus.UNAUTHORIZED;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
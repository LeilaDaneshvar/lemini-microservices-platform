package com.lemini.users.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.lemini.users.ui.model.response.ApiErrorResponse;

import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom user service exceptions
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleUserServiceException(
            UserServiceException ex,
            WebRequest request) {

        HttpStatus status = getHttpStatusFromErrorType(ex.getErrorType());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED; // Default 401
        String errorMessage = "{auth.message.unauthorized}";

        // Logic to switch to 400 if it was a validation/service error
        if (ex instanceof AuthenticationServiceException) {
            status = HttpStatus.BAD_REQUEST; // Switch to 400
            errorMessage = "{auth.message.bad_request}";
        }

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(errorMessage)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path("")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        // all other exceptions
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .path("")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private HttpStatus getHttpStatusFromErrorType(UserServiceException.UserErrorType type) {
        switch (type) {
            case USER_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case EMAIL_ALREADY_EXISTS:
                return HttpStatus.CONFLICT;
            case VALIDATION_ERROR:
                return HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN:
                return HttpStatus.UNAUTHORIZED;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
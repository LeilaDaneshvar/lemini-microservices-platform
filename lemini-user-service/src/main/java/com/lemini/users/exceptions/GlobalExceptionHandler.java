package com.lemini.users.exceptions;

import org.springframework.http.ResponseEntity;
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
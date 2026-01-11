package com.lemini.users.exceptions;


import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public enum UserErrorType {
        USER_NOT_FOUND("{user.err.not_found}"),
        EMAIL_ALREADY_EXISTS("{user.err.email_exists}"),
        VALIDATION_ERROR("{user.err.validation}");

        private final String defaultMessage;

        UserErrorType(String message) {
            this.defaultMessage = message;
        }

        public String getMessage() {
            return defaultMessage;
        }

        public String getKey() {
            return this.name();
        }
    }

    private final UserErrorType errorType;


    public UserServiceException(UserErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

}

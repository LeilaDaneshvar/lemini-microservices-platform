package com.lemini.users.validation;

public final class ValidationConstants {

    private ValidationConstants() {
        // Private constructor to prevent instantiation
    }

    public static final int FIRST_NAME_MIN_LENGTH = 2;
    public static final int FIRST_NAME_MAX_LENGTH = 50;
    public static final int LAST_NAME_MIN_LENGTH = 2;
    public static final int LAST_NAME_MAX_LENGTH = 50;

    public static final int EMAIL_MAX_LENGTH = 254;

    public static final int PASSWORD_MIN_LENGTH = 12;
    public static final int PASSWORD_MAX_LENGTH = 64;

}

package com.lemini.users.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private final EmailValidator emailValidator = new EmailValidator();

    @ParameterizedTest
    @ValueSource(strings = {
        "user@example.com",
        "name.surname@company.co.uk",
        "valid_email123@domain.org",
        "user+test@example.com",
        "user@mail.example.com",
        "USER@EXAMPLE.COM",
        "user@example.technology"
    })
    void shouldReturnTrueForValidEmail(String email) {
        assertTrue(emailValidator.test(email));
    }

    @ParameterizedTest
    @ValueSource(strings={
        "invalid-email",
        "user@domain",
        "@domain.com",
        "user@.com",
        " ",
        " user@example.com ",
        "user@@example.com",
        "user..name@example.com",
        ".user@example.com",
        "user@example..com"
    })
    void shouldReturnFalseForInvalidEmail(String email) {
        assertFalse(emailValidator.test(email));
    }
}

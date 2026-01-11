package com.lemini.users.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private final EmailValidator emailValidator = new EmailValidator();

    @Test
    void shouldReturnTrueForValidEmail() {
        assertTrue(emailValidator.test("user@example.com"));
        assertTrue(emailValidator.test("name.surname@company.co.uk"));
        assertTrue(emailValidator.test("valid_email123@domain.org"));
    }

    @Test
    void shouldReturnFalseForInvalidEmail() {
        assertFalse(emailValidator.test("invalid-email"));
        assertFalse(emailValidator.test("user@domain"));
        assertFalse(emailValidator.test("@domain.com"));
        assertFalse(emailValidator.test("user@.com"));
        assertFalse(emailValidator.test(null));
        assertFalse(emailValidator.test(""));
    }
}

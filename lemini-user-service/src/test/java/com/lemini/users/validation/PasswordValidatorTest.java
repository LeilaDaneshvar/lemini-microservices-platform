package com.lemini.users.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PasswordValidatorTest {
    
    private final PasswordValidator passwordValidator = new PasswordValidator();
    
    @Test
    public void testValidPassword() {
        assertTrue(passwordValidator.test("ValidPassword123!"));
    }

    @Test
    public void testInvalidPassword() {
        assertFalse(passwordValidator.test("short"));
        assertFalse(passwordValidator.test(null));

    }
}
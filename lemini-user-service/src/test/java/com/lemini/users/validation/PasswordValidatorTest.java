package com.lemini.users.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PasswordValidatorTest {
    
    private final PasswordValidator passwordValidator = new PasswordValidator();
    
    @ParameterizedTest 
    @ValueSource (strings = {
        "Password1!",
        "ValidPass123@",
        "Abcdef1$",
        "StrongPassword99&"
    })
    public void testValidPassword(String password) {
        assertTrue(passwordValidator.test(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "   ",
        "short",
        "Short1!",          // less than 8 characters
        "password1!",       // no uppercase
        "PASSWORD1!",       // no lowercase
        "Password!",        // no number
        "Password123",      // no special character
        "Password 1!",      // space not allowed
        "Password1#"        // # not allowed 
    })
    public void testInvalidPassword(String password) {
        assertFalse(passwordValidator.test(password));

    }
}
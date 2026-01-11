package com.lemini.users.ui.model.request;

import com.lemini.users.shared.enums.AddressType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRequestModelValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserRequestModel createValidUser() {
        // Helper to create a user where only the email needs to be changed for testing
        AddressRequestModel address = new AddressRequestModel("City", "Country", "Street", "12345", AddressType.BILLING);
        return new UserRequestModel(
                "John",
                "Doe",
                "john.doe@example.com",
                "Password123!",
                List.of(address)
        );
    }

    @Test
    void shouldPassValidationForValidEmail() {
        UserRequestModel user = createValidUser();
        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void shouldFailValidationForInvalidEmail() {
        // Invalid email format
        AddressRequestModel address = new AddressRequestModel("City", "Country", "Street", "12345", AddressType.BILLING);
        UserRequestModel user = new UserRequestModel(
                "John",
                "Doe",
                "invalid-email",
                "Password123!",
                List.of(address)
        );

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);
        
        // We expect at least one violation on the "email" path
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        
        assertTrue(hasEmailError, "Expected validation error for invalid email");
    }
}

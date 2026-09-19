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

        return new UserRequestModel(
                "John",
                "Doe",
                "john.doe@example.com",
                "Password123!",
                List.of(createValidAddress()));

    }

    private AddressRequestModel createValidAddress() {

        return new AddressRequestModel(
                "City",
                "Country",
                "Street",
                "12345",
                AddressType.BILLING);

    }

    private boolean hasViolationFor(
            Set<ConstraintViolation<UserRequestModel>> violations,
            String field) {

        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    @Test
    void shouldPassValidationForValidUser() {
        UserRequestModel user = createValidUser();

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void shouldFailValidationForInvalidFirstName() {
        // Invalid first name

        UserRequestModel user = new UserRequestModel(
                "",
                "Doe",
                "john.doe@example.com",
                "Password123!",
                List.of(createValidAddress()));

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "firstName"), "Expected validation error for invalid first name");
    }

    @Test
    void shouldFailValidationForInvalidLastName() {
        // Invalid last name
        UserRequestModel user = new UserRequestModel(
                "John",
                "",
                "john.doe@example.com",
                "Password123!",
                List.of(createValidAddress()));

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "lastName"), "Expected validation error for invalid last name");
    }

    @Test
    void shouldFailValidationForInvalidEmail() {
        // Invalid email format
        UserRequestModel user = new UserRequestModel(
                "John",
                "Doe",
                "invalid-email",
                "Password123!",
                List.of(createValidAddress()));

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "email"), "Expected validation error for invalid email");
    }

    @Test
    void shouldFailValidationForInvalidPassword() {
        // Invalid password format
        UserRequestModel user = new UserRequestModel(
                "John",
                "Doe",
                "john.doe@example.com",
                "short",
                List.of(createValidAddress()));

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "password"), "Expected validation error for invalid password");
    }

    @Test
    void shouldFailValidationForEmptyAddresses() {
        // Invalid address
        UserRequestModel user = new UserRequestModel(
                "John",
                "Doe",
                "john.doe@example.com",
                "Password123!",
                List.of());

        Set<ConstraintViolation<UserRequestModel>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "addresses"), "Expected validation error for invalid address");
    }

}

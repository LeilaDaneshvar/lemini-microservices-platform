package com.lemini.users.validation;

import jakarta.validation.ConstraintValidator;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, jakarta.validation.ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator();
        return validator.test(password);
    }

}

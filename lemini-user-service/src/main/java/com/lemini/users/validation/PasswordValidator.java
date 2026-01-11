package com.lemini.users.validation;

import java.util.function.Predicate;

public class PasswordValidator implements Predicate<String> {

    @Override
    public boolean test(String password) {
        // Password must be at least 8 characters long, contain at least one uppercase letter,
        // one lowercase letter, one number, and one special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password != null && password.matches(passwordRegex);
    }

}

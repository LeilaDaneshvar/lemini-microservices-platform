package com.lemini.users.validation;

import java.util.function.Predicate;

public class EmailValidator implements Predicate<String> {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*"
          + "@"
          + "[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$";

    @Override
    public boolean test(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

}

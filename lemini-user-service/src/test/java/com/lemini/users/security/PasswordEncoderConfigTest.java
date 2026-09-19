package com.lemini.users.security;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordEncoderConfigTest {

    private final PasswordEncoderConfig passwordEncoderConfig = new PasswordEncoderConfig();
    private final PasswordEncoder passwordEncoder = passwordEncoderConfig.passwordEncoder();

    @Test
    public void passwordEncoder_shouldUseBCryptStrength10() {
        String rawPassword = "TestPassword123!";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(encodedPassword.matches("^\\$2[aby]\\$10\\$.*"));
    }

    @Test
    void passwordEncoder_shouldEncodeAndMatchPassword() {
        String rawPassword = "TestPassword123!";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertNotEquals(rawPassword, encodedPassword);
    }

}

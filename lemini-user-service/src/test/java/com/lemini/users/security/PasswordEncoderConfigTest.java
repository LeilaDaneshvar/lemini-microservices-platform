package com.lemini.users.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderConfigTest {

    PasswordEncoderConfig passwordEncoderConfig = new PasswordEncoderConfig();
    PasswordEncoder passwordEncoder = passwordEncoderConfig.passwordEncoder();

    @Test
    public void passwordEncoder_ShouldUseBCryptPasswordEncoderStrength10() {
        String rawPassword = "TestPassword123!";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(encodedPassword.matches("^\\$2[aby]\\$10\\$.*"));
    }

    @Test
    void passwordEncoder_shouldEncodeAndMatchPassword() {
        String rawPassword = "TestPassword123!";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

}

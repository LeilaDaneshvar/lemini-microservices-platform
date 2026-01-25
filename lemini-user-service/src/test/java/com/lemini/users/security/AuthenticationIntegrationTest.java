package com.lemini.users.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.ui.model.request.UserLoginRequestModel;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@test.com");
        user.setUserId("test-user-id");
        user.setEmailVerificationStatus(true);
        user.setEncryptedPassword(passwordEncoder.encode("Password123!"));
        userRepository.save(user);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        UserLoginRequestModel loginRequest = new UserLoginRequestModel("test@test.com", "Password123!");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(jsonPath("$.userId").value("test-user-id"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        UserLoginRequestModel loginRequest = new UserLoginRequestModel("test@test.com", "WrongPassword123!");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailLoginWithInvalidInput() throws Exception {
        // Invalid email format
        UserLoginRequestModel loginRequest = new UserLoginRequestModel("invalid-email", "Password123!");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}

package com.lemini.users.integration;

import java.sql.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.lemini.users.io.entity.AddressEntity;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.security.SecurityConstants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.properties (e.g., H2 DB)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String validToken;
    private final String userId = "user-123-abc";
    private String tokenSecret;

    @Autowired
    private Environment env;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        tokenSecret = env.getProperty("app.security.tokenSecret");

        // 1. Save a real user to the database
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setFirstName("Leila");
        userEntity.setLastName("Daneshvar");
        userEntity.setEmail("leila@example.com");
        userEntity.setEncryptedPassword("hashed_password_here");
        userEntity.setEmailVerificationStatus(true);
        userEntity.setEmailVerificationToken("some_verification_token");

        AddressEntity address = new AddressEntity();
        address.setAddressId("12345");
        address.setType("BILLING");
        address.setCity("Tehran");
        address.setCountry("Iran");
        address.setStreetName("Some Street");
        address.setPostalCode("12345");
        address.setUserProfile(userEntity);

        userEntity.setAddresses(List.of(address));

        userRepository.save(userEntity);

        Instant now = Instant.now();

        // 2. Generate a real JWT for this user
        byte[] signingKey = Base64.getDecoder().decode(tokenSecret);
        validToken = Jwts.builder()
                .subject(userEntity.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME)))
                .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)
                .claim("userId", userId)
                .compact();

    }

    @Test
    @DisplayName("GET /users/{id} - Success: End-to-end retrieval with security")
    void getUserProfile_Success() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", userId)
            .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + validToken)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.email").value("leila@example.com"))
            // CRITICAL: Ensure password fields are NOT in the response body
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.encryptedPassword").doesNotExist());
    }
}
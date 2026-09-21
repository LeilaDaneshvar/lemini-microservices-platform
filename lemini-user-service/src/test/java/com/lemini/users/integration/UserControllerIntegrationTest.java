package com.lemini.users.integration;

import java.util.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.lemini.users.io.entity.AddressEntity;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.repository.UserRepository;
import com.lemini.users.security.SecurityConstants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.properties (e.g., H2 DB)
class UserControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

         @Autowired
        private Environment env;

        private String validToken;

        private static final String USER_ID = "user-123-abc";
        private static final String TEST_EMAIL = "leila@example.com";
        private static final String BASE_URL = "/api/v1/users";

        @BeforeEach
        void setUp() {
                userRepository.deleteAll();
                String tokenSecret = Objects.requireNonNull(env.getProperty("app.security.tokenSecret")
                                        , "Token secret must not be null");

                // 1. Save a real user to the database
                UserEntity userEntity = new UserEntity();
                userEntity.setUserId(USER_ID);
                userEntity.setFirstName("Leila");
                userEntity.setLastName("Daneshvar");
                userEntity.setEmail(TEST_EMAIL);
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
                                .claim("userId", USER_ID)
                                .compact();

        }

        @Test
        @DisplayName("GET /users/{id} - Success: End-to-end retrieval with security")
        void getUserProfile_Success() throws Exception {
                mockMvc.perform(get(BASE_URL + "/{userId}", USER_ID)
                                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + validToken)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(USER_ID))
                                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                                .andExpect(jsonPath("$.password").doesNotExist())
                                .andExpect(jsonPath("$.encryptedPassword").doesNotExist())
                                .andExpect(jsonPath("$.emailVerificationToken").doesNotExist());
                                
        }

        @Test
        @DisplayName("PUT /users/{id} - success: End-to-end update with security")
        void updateUserProfile_Success() throws Exception {
                String updateJson = """
                                {
                                    "firstName": "LeilaUpdated",
                                    "lastName": "DaneshvarUpdated"
                                }
                                """;

                mockMvc.perform(put(BASE_URL + "/{userId}", USER_ID)
                                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("LeilaUpdated"))
                                .andExpect(jsonPath("$.lastName").value("DaneshvarUpdated"));

                UserEntity updatedUser = userRepository.findByUserId(USER_ID)
                                .orElseThrow();

                assertEquals("LeilaUpdated", updatedUser.getFirstName());
                assertEquals("DaneshvarUpdated", updatedUser.getLastName());
        }

        @Test
        @DisplayName("DELETE /users/{id} - success: End-to-end deletion with security")
        void deleteUserProfile_Success() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/{userId}", USER_ID)
                                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + validToken)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("SUCCESS"))
                                .andExpect(jsonPath("$.name").value("DELETE"))
                                .andExpect(jsonPath("$.result").value("User deleted successfully"));

                assertTrue(userRepository.findByUserId(USER_ID).isEmpty());
        }

        @Test
        @DisplayName("GET /users - success: End-to-end paginated retrieval with security")
        void getUsers_Paginated_Success() throws Exception {
                mockMvc.perform(get(BASE_URL)
                                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + validToken)
                                .param("page", "1")
                                .param("limit", "10")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.users[0].userId").value(USER_ID))
                                .andExpect(jsonPath("$.users[0].email").value(TEST_EMAIL))
                                .andExpect(jsonPath("$.users[0].password").doesNotExist())
                                .andExpect(jsonPath("$.users[0].encryptedPassword").doesNotExist())
                                .andExpect(jsonPath("$.users[0].emailVerificationToken").doesNotExist());
        }
}
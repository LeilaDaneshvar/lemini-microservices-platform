package com.lemini.users.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerExceptionResolver;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.lemini.users.service.UserService;
import com.lemini.users.shared.dto.UserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;

// @SpringBootTest
//  @AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CustomAuthorizationFilterTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtParser jwtParser;

    @Mock
    // @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    private CustomAuthorizationFilter authorizationFilter;

    @RestController
    @RequestMapping("/api/v1/users")
    static class DummyController {
        @GetMapping("/{userId}")
        public ResponseEntity<String> getUser(@PathVariable String userId) {
            return ResponseEntity.ok("ok");
        }
    }

    @BeforeEach
    void setUp() {
        // 1. Manually create the filter with mocks
        authorizationFilter = new CustomAuthorizationFilter(userService, jwtParser, handlerExceptionResolver);

        // 2. Build MockMvc with ONLY this filter
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .addFilters(authorizationFilter)
                .setHandlerExceptionResolvers(handlerExceptionResolver)
                .build();
        // 3. Mock the resolver to just set response status
        org.mockito.Mockito.lenient().doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(handlerExceptionResolver).resolveException(
                any(), any(), any(), any());

    }

    @Test
    @DisplayName("Should authorize user with valid JWT")
    void shouldAuthorizeWithValidToken() throws Exception {
        // 1. Mock the JWT parsing
        String mockToken = SecurityConstants.TOKEN_PREFIX + "valid.token";
        Claims claims = Jwts.claims().subject("user-123").build();
        Jws<Claims> jws = mock(Jws.class);

        when(jwtParser.parseSignedClaims(anyString())).thenReturn(jws);
        when(jws.getPayload()).thenReturn(claims);

        // 2. Mock the User Service
        UserDto userDto = new UserDto(0L, "user-123", "", "", "", "", "", "", true, List.of()); // fill other required
                                                                                                // fields as per your
                                                                                                // record definition
        when(userService.getUserByUserId("user-123")).thenReturn(userDto);

        // 3. Execute request to a protected endpoint
        mockMvc.perform(get("/api/v1/users/{userId}", "user-123")
                .header(SecurityConstants.HEADER_STRING, mockToken))
                .andExpect(status().isOk());

        // Verify context was set (indirectly by passing security check)
    }

    @Test
    @DisplayName("Should delegate to ExceptionResolver on invalid token")
    void shouldFailOnInvalidToken() throws Exception {
        // 1. Force the parser to throw an exception
        when(jwtParser.parseSignedClaims(anyString()))
                .thenThrow(new ExpiredJwtException(null, null, "Expired"));

        // 2. Execute request
        mockMvc.perform(get("/api/v1/users/{userId}", "user-123")
                .header(SecurityConstants.HEADER_STRING, "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
        // Assuming your GlobalExceptionHandler maps INVALID_TOKEN to 401
    }

}

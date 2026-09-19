package com.lemini.users.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerExceptionResolver;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;

import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.service.UserService;
import com.lemini.users.shared.dto.UserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;

// Test class for CustomAuthorizationFilter
// How it works:
// 1. The test sets up a MockMvc instance with a dummy controller and the custom authorization filter.
// 2. It mocks the JWT parser and the user service to simulate different scenarios.
// 3. The test methods perform HTTP requests and verify the behavior of the authorization filter.

@ExtendWith(MockitoExtension.class)
class CustomAuthorizationFilterTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtParser jwtParser;

    @Mock
    // @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    private CustomAuthorizationFilter authorizationFilter;

    private static final String BASE_URL = "/api/v1/users";

    @RestController
    @RequestMapping(BASE_URL)
    static class DummyController {

        @GetMapping("/{userId}")
        ResponseEntity<String> getUser(@PathVariable String userId) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserDto principal = (UserDto) authentication.getPrincipal();

            return ResponseEntity.ok(principal.userId());
        }
    }

    @BeforeEach
    void setUp() {

        authorizationFilter = new CustomAuthorizationFilter(
                userService,
                jwtParser,
                handlerExceptionResolver);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new DummyController())
                .addFilters(authorizationFilter)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authorize user with valid JWT")
    void shouldAuthorizeWithValidToken() throws Exception {

        // Given
        String mockToken = SecurityConstants.TOKEN_PREFIX + "valid.token";

        Claims claims = Jwts.claims()
                .add("userId", "user-123")
                .build();

        Jws<Claims> jws = mock(Jws.class);

        when(jwtParser.parseSignedClaims("valid.token")).thenReturn(jws);
        when(jws.getPayload()).thenReturn(claims);

        UserDto userDto = new UserDto(
                0L,
                "user-123",
                "",
                "",
                "",
                "",
                "",
                "",
                true,
                List.of());

        when(userService.getUserByUserId("user-123")).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{userId}", "user-123")
                .header(SecurityConstants.HEADER_STRING, mockToken))
                .andExpect(status().isOk())
                .andExpect(content().string("user-123"));

        verify(jwtParser).parseSignedClaims("valid.token");
        verify(userService).getUserByUserId("user-123");
        verify(handlerExceptionResolver, never()).resolveException(any(), any(), isNull(), any());
    }

    @Test
    @DisplayName("Should delegate invalid token to ExceptionResolver")
    void shouldFailOnInvalidToken() throws Exception {

        // Given
        when(jwtParser.parseSignedClaims("expired-token"))
                .thenThrow(new ExpiredJwtException(
                        null,
                        null,
                        "Expired"));

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(handlerExceptionResolver)
                .resolveException(any(), any(), isNull(), any());

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{userId}", "user-123")
                .header(
                        SecurityConstants.HEADER_STRING,
                        "Bearer expired-token"))
                .andExpect(status().isUnauthorized());

        verify(jwtParser).parseSignedClaims("expired-token");
        verify(userService, never()).getUserByUserId(anyString());

        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
        verify(handlerExceptionResolver).resolveException(any(),any(),isNull(),exceptionCaptor.capture());
        Exception capturedException = exceptionCaptor.getValue();
        UserServiceException exception = assertInstanceOf(UserServiceException.class, capturedException);

        assertEquals(UserServiceException.UserErrorType.INVALID_TOKEN, exception.getErrorType());


    }

}

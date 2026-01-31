package com.lemini.users.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.lemini.users.exceptions.UserServiceException;
import com.lemini.users.service.UserService;
import com.lemini.users.shared.dto.UserDto;

import io.jsonwebtoken.JwtParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtParser jwtParser;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authentication = doAuthorization(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (Exception ex) {
            // Because we are in a Filter, we MUST use the resolver to hit @ControllerAdvice
            resolver.resolveException(request, response, null, ex);
        }

    }

    private UsernamePasswordAuthenticationToken doAuthorization(HttpServletRequest request) {

        String token = request.getHeader(SecurityConstants.HEADER_STRING)
                .replace(SecurityConstants.TOKEN_PREFIX, "");

        try {
            String userId = jwtParser
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("userId", String.class);

            if (userId == null)
                return null;

            UserDto userDto = userService.getUserByUserId(userId);

            // Return token with user details and empty authorities list
            return new UsernamePasswordAuthenticationToken(userDto, null, List.of());

        } catch (UserServiceException e) {
            throw e; // 404 - for user not found
        } catch (Exception e) {
            // Catch ExpiredJwtException, SignatureException, etc. //401
            throw new UserServiceException(UserServiceException.UserErrorType.INVALID_TOKEN);
        }

    }

}

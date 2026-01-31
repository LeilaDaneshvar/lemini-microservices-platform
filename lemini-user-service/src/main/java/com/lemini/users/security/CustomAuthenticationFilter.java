package com.lemini.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lemini.users.ui.model.request.UserLoginRequestModel;
import com.lemini.users.ui.model.response.AuthenticationResponseModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final AuthenticationManager authenticationManager;
    private final Validator validator;
    private final HandlerExceptionResolver resolver;

    

    public CustomAuthenticationFilter(
        AuthenticationManager authenticationManager,
        Validator validator,
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.validator = validator;
        this.resolver = resolver;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            String contentType = request.getContentType();
            UserLoginRequestModel loginRequest;

            if (contentType != null && contentType.contains(MediaType.APPLICATION_XML_VALUE)) {
                loginRequest = new XmlMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
            } else {
                loginRequest = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(request.getInputStream(), UserLoginRequestModel.class);
            }

            // Validation
            Set<ConstraintViolation<UserLoginRequestModel>> validations = validator.validate(loginRequest);
            if (!validations.isEmpty()) {
                throw new AuthenticationServiceException(validations.iterator().next().getMessage());
            }

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password(),
                            new ArrayList<>()));

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        resolver.resolveException(request, response, null, failed);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

                CustomUser user = (CustomUser) authResult.getPrincipal();

                Instant now = Instant.now();

                byte[] signingKey = Base64.getDecoder().decode(SecurityConstants.getTokenSecret());

                String accessToken = Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME)))
                    .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)
                    .claim("userId", user.getUserId())
                    .compact();

                response.setHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                
                AuthenticationResponseModel authResponse = new AuthenticationResponseModel(
                    user.getUserId(), accessToken);

                String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

                if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
                    response.setContentType(MediaType.APPLICATION_XML_VALUE);
                    new XmlMapper().registerModule(new JavaTimeModule()).writeValue(response.getOutputStream(), authResponse);
                } else {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().registerModule(new JavaTimeModule()).writeValue(response.getOutputStream(), authResponse);
                }

    }

}

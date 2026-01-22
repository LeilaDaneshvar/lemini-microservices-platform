package com.lemini.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemini.users.ui.model.request.UserLoginRequestModel;
import com.lemini.users.ui.model.response.ApiErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Validator validator;
    private final MessageSource messageSource;

    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            String contentType = request.getContentType();
            UserLoginRequestModel loginRequest;

            if (contentType != null && contentType.contains(MediaType.APPLICATION_XML_VALUE)) {
                loginRequest = new XmlMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
            } else {
                loginRequest = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
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

        } catch (Exception e) {

            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
                
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        String errorMessage = messageSource.getMessage("auth.message.unauthorized", null, Locale.getDefault());

        // Check if the failure was caused by validation (AuthenticationServiceException)
        if (failed instanceof AuthenticationServiceException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            errorMessage = messageSource.getMessage("auth.message.validation_error", null, Locale.getDefault());
        }

        response.setStatus(status);

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(status)
                .error(errorMessage)
                .message(failed.getMessage())
                .path(request.getServletPath())
                .build();
        
        String acceptHeader = request.getHeader("Accept");

        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
            response.setContentType(MediaType.APPLICATION_XML_VALUE);
            new XmlMapper().writeValue(response.getOutputStream(), errorResponse);
        } else {
             // Default to JSON for everything else (including wildcards * / * or text/plain)
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

    }

}

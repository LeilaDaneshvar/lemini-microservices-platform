package com.lemini.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.validation.Validator;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import com.lemini.users.service.UserService;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final PasswordEncoderConfig passwordEncoder;
    private final Validator validator;
    private final HandlerExceptionResolver resolver;

    private final String tokenSecret;

    public WebSecurityConfig(UserService userService,
                             PasswordEncoderConfig passwordEncoder,
                             Validator validator,
                             @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                             @Value("${app.security.tokenSecret}") String tokenSecret) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.resolver = resolver;
        this.tokenSecret = tokenSecret;
    }

    @Bean
    public CustomAuthorizationFilter customAuthorizationFilter(JwtParser jwtParser) {
        return new CustomAuthorizationFilter(userService, jwtParser, resolver);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(passwordEncoder.bCryptPasswordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtParser jwtParser) throws Exception {

        AuthenticationManager authManager = authenticationManager(http);

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
            authManager,
            validator,
            resolver
        );

        customAuthenticationFilter.setFilterProcessesUrl(SecurityConstants.SIGN_IN_URL);

        http
                .csrf(csrf -> csrf.disable()) // stateless JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll() // Allow Registration
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // Allow H2 Console
                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Allow frames for H2
                .authenticationManager(authManager) // Inject the manager
                // Add login filter to the chain
                .addFilter(customAuthenticationFilter)
                // Add authorization filter to the chain - position it explicitly
                .addFilterAfter(customAuthorizationFilter(jwtParser), CustomAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Force JWT mode

        return http.build();
    }

    @Bean
    public JwtParser jwtParser() {
        byte[] signingKey = Base64.getDecoder().decode(tokenSecret.getBytes());
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(signingKey)).build();
    }

}

package com.lemini.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.MessageSource;
import jakarta.validation.Validator;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

import com.lemini.users.service.UserService;

import lombok.AllArgsConstructor;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final UserService userService;
    private final PasswordEncoderConfig passwordEncoder;
    private final Validator validator;
    private final MessageSource messageSource;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        
        authenticationManagerBuilder.userDetailsService(userService)
               .passwordEncoder(passwordEncoder.bCryptPasswordEncoder());
        
        return authenticationManagerBuilder.build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        AuthenticationManager authManager = authenticationManager(http);

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authManager, validator, messageSource);

        customAuthenticationFilter.setFilterProcessesUrl(SecurityConstants.SIGN_IN_URL);

        http
                .csrf(csrf -> csrf.disable()) // stateless JWT
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/users").permitAll() // Allow Registration
                    .requestMatchers(PathRequest.toH2Console()).permitAll() // Allow H2 Console
                    .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Allow frames for H2
                .authenticationManager(authManager) // Inject the manager
                .addFilter(customAuthenticationFilter) // Add login filter to the chain
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    ); // Force JWT mode
                    
        return http.build();
    }
    


}

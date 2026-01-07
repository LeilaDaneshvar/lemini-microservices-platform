package com.lemini.users.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864_000_000; //10 days
    public static final String TOKEN_PREFIX = "Bearer ";

    private static String TOKEN_SECRET;

    @Value("${app.security.tokenSecret}")
    public void setTokenSecret(String tokenSecret) {
        TOKEN_SECRET = tokenSecret;
    }    

    public static String getTokenSecret() {
        return TOKEN_SECRET;
    }
}

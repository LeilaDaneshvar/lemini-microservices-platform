package com.lemini.users.shared;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.lemini.users.security.SecurityConstants;


@Component 
public class VerificationTokenGenerator {

     public String generateEmailVerificationToken(String userId) {
        String token = Jwts.builder()
                .subject(userId)
                .expiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)) 
                .signWith(Keys.hmacShaKeyFor(SecurityConstants.getTokenSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
        return token;
    }
    
}

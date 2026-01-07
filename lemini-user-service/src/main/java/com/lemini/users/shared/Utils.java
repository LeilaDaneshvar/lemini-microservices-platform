package com.lemini.users.shared;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.lemini.users.security.SecurityConstants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    public String generateEmailVerificationToken(String userId) {
    String token = Jwts.builder()
            .subject(userId)
            .expiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)) 
            .signWith(Keys.hmacShaKeyFor(SecurityConstants.getTokenSecret().getBytes(StandardCharsets.UTF_8)))
            .compact();
    return token;
}

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

}

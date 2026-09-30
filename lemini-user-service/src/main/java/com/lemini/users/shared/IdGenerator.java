package com.lemini.users.shared;

import java.security.SecureRandom;
import java.util.Random;


public final class IdGenerator {

    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int DEFAULT_USER_ID_LENGTH = 30;
    private static final int DEFAULT_ADDRESS_ID_LENGTH = 30;
    
    private IdGenerator() {
    }

    public static String generateUserId() {
        return generateRandomString(DEFAULT_USER_ID_LENGTH);
    }

    public static String generateAddressId() {
        return generateRandomString(DEFAULT_ADDRESS_ID_LENGTH);
    }

    private static String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return returnValue.toString();
    }
    
}

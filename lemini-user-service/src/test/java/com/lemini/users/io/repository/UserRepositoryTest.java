package com.lemini.users.io.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.lemini.users.io.entity.AddressEntity;
import com.lemini.users.io.entity.UserEntity;

@DataJpaTest // This sets up an in-memory H2 DB automatically
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private static final String TEST_EMAIL = "user1@example.com";
    private static final String TEST_USER_ID = "user1publicId";
    private static final String NONEXISTENT_EMAIL = "nonexistent@example.com";
    private static final String NONEXISTENT_USER_ID = "nonexistentUserId";

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(TEST_USER_ID);
        userEntity.setFirstName("User1");
        userEntity.setLastName("Family1");
        userEntity.setEmail(TEST_EMAIL);
        userEntity.setEncryptedPassword("encryptedPassword");

        AddressEntity address = new AddressEntity();
        address.setAddressId("address1Id");
        address.setType("shipping");
        address.setCity("City1");
        address.setCountry("Country1");
        address.setPostalCode("12345");
        address.setStreetName("123 Main St");
        address.setUserProfile(userEntity);
        List<AddressEntity> addresses = List.of(address);

        userEntity.setAddresses(addresses);

        userRepository.save(userEntity);
    }

    @Test
    void findByEmail_whenUserExists_shouldReturnUser() {
        // When
        String email = TEST_EMAIL;
        Optional<UserEntity> foundUserOptional = userRepository.findByEmail(email);

        // Then
        assertTrue(foundUserOptional.isPresent());
        UserEntity foundUser = foundUserOptional.get();
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void findByEmail_whenUserDoesNotExist_shouldReturnEmpty() {
        // When
        String email = NONEXISTENT_EMAIL;
        Optional<UserEntity> foundUserOptional = userRepository.findByEmail(email);

        // Then
        assertTrue(foundUserOptional.isEmpty());
    }

    @Test
    void findByUserId_whenUserExists_shouldReturnUser() {
        // When
        String userId = TEST_USER_ID;
        Optional<UserEntity> foundUserOptional = userRepository.findByUserId(userId);

        // Then
        assertTrue(foundUserOptional.isPresent());
        UserEntity foundUser = foundUserOptional.get();
        assertEquals(userId, foundUser.getUserId());
    }

    @Test
    void findByUserId_whenUserDoesNotExist_shouldReturnEmpty() {
        // When
        String userId = NONEXISTENT_USER_ID;
        Optional<UserEntity> foundUserOptional = userRepository.findByUserId(userId);

        // Then
        assertTrue(foundUserOptional.isEmpty());
    }
}
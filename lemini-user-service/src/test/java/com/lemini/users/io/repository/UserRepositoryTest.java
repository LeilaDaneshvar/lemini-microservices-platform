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
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId("user1publicId"); 
        userEntity.setFirstName("User1");
        userEntity.setLastName("Family1");
        userEntity.setEmail("user1@example.com");
        userEntity.setEncryptedPassword("encryptedPassword");

        List<AddressEntity> addresses = List.of();

        AddressEntity address = new AddressEntity();
        address.setAddressId("address1Id");
        address.setType("shipping");
        address.setCity("City1");
        address.setCountry("Country1");
        address.setPostalCode("12345");
        address.setStreetName("123 Main St");
        address.setUserProfile(userEntity);
        addresses = List.of(address);

        userEntity.setAddresses(addresses);

        userRepository.save(userEntity);
    }

    @Test
    void testFindByEmail() {
        String email = "user1@example.com";
        Optional<UserEntity> foundUserOptional = userRepository.findByEmail(email);
        assertTrue(foundUserOptional.isPresent());
        UserEntity foundUser = foundUserOptional.get();
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        String email = "nonexistent@example.com";
        Optional<UserEntity> foundUserOptional = userRepository.findByEmail(email);
        assertTrue(foundUserOptional.isEmpty());
    }

    @Test
    void testFindByUserId() {
        String userId = "user1publicId";
        Optional<UserEntity> foundUserOptional = userRepository.findByUserId(userId);
        assertTrue(foundUserOptional.isPresent());
        UserEntity foundUser = foundUserOptional.get();
        assertEquals(userId, foundUser.getUserId());
    }

    @Test
    void testFindByUserId_NotFound() {
        String userId = "nonexistentUserId";
        Optional<UserEntity> foundUserOptional = userRepository.findByUserId(userId);
        assertTrue(foundUserOptional.isEmpty());
    }
}
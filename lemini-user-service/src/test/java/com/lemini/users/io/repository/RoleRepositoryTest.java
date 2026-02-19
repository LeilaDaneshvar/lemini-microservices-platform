package com.lemini.users.io.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.lemini.users.io.entity.RoleEntity;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private RoleEntity role;

    @BeforeEach
    void setUp() {
        role = new RoleEntity();
        role.setName("ROLE_USER");
        // No need to manually flush usually, but it's fine if you want to force it
        roleRepository.save(role);
    }

    @Test
    @DisplayName("Should find Role by Name when it exists")
    void testFindByName_ReturnsRole() {
        // When
        Optional<RoleEntity> foundRole = roleRepository.findByName("ROLE_USER");

        // Then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("Should return empty Optional when Role name does not exist")
    void testFindByName_ReturnsEmpty() {
        // When
        Optional<RoleEntity> foundRole = roleRepository.findByName("NON_EXISTENT");

        // Then
        assertThat(foundRole).isNotPresent();
    }
}
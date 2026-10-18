package com.lemini.users.initializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lemini.users.io.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lemini.users.io.entity.AuthorityEntity;
import com.lemini.users.io.entity.RoleEntity;
import com.lemini.users.io.entity.UserEntity;
import com.lemini.users.io.repository.AuthorityRepository;
import com.lemini.users.io.repository.RoleRepository;
import com.lemini.users.shared.IdGenerator;
import com.lemini.users.shared.enums.Authorities;
import com.lemini.users.shared.enums.Roles;

import lombok.RequiredArgsConstructor;

@Component
@Profile("demo")
@RequiredArgsConstructor
public class SetupDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Add Admin User
        addAdminUser();

    }


    private void addAdminUser() {
        if (userRepository.findByEmail("admin@admin.com").isPresent()) {
            return;
        }

        UserEntity adminUser = new UserEntity();

        adminUser.setUserId(IdGenerator.generateUserId().toString());
        adminUser.setFirstName("admin");
        adminUser.setLastName("admin");
        adminUser.setEmail("admin@admin.com");
        adminUser.setEncryptedPassword(passwordEncoder.encode("123456"));
        adminUser.setEmailVerificationStatus(true);
        adminUser.setRoles(new ArrayList<>(List.of(roleRepository.findByName(Roles.ADMIN.name()).orElseThrow())));

        userRepository.save(adminUser);
    }
}

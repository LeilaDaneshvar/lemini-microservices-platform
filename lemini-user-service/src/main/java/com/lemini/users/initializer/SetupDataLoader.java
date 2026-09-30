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

    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. Create Authorities
        AuthorityEntity readAuthority = createAuthorityIfNotFound(Authorities.READ_AUTHORITY);
        AuthorityEntity writeAuthority = createAuthorityIfNotFound(Authorities.WRITE_AUTHORITY);
        AuthorityEntity deleteAuthority = createAuthorityIfNotFound(Authorities.DELETE_AUTHORITY);
        AuthorityEntity updateAuthority = createAuthorityIfNotFound(Authorities.UPDATE_AUTHORITY);

        // 2. Create Roles 
        // use mutable lists: Hibernate's merge clears/replaces the collection in place
        createRoleIfNotFound(Roles.ADMIN,
                new ArrayList<>(List.of(readAuthority, writeAuthority, deleteAuthority, updateAuthority)));
        createRoleIfNotFound(Roles.USER, new ArrayList<>(List.of(readAuthority, updateAuthority)));

        // 3. Add Admin User
        addAdminUser();

    }

    private void createRoleIfNotFound(Roles roleEnum, Collection<AuthorityEntity> authorities) {
        roleRepository.findByName(roleEnum.name()).ifPresentOrElse(role -> {
            // Update logic: Ensure existing roles get new authorities if added to the code
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }, () -> {
            RoleEntity role = new RoleEntity();
            role.setName(roleEnum.name());
            role.setAuthorities(authorities);
            roleRepository.save(role);
        });
    }

    private AuthorityEntity createAuthorityIfNotFound(Authorities authorityEnum) {
        String name = authorityEnum.name();
        return authorityRepository.findByName(name).orElseGet(() -> {
            AuthorityEntity authority = new AuthorityEntity();
            authority.setName(name);
            authorityRepository.save(authority);
            return authority;
        });
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

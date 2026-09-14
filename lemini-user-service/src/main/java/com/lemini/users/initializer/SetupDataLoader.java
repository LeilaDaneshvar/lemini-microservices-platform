package com.lemini.users.initializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lemini.users.io.entity.AuthorityEntity;
import com.lemini.users.io.entity.RoleEntity;
import com.lemini.users.io.repository.AuthorityRepository;
import com.lemini.users.io.repository.RoleRepository;

@Component
public class SetupDataLoader implements CommandLineRunner {


    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    SetupDataLoader(AuthorityRepository authorityRepository, RoleRepository roleRepository) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. Create Authtorities
        AuthorityEntity readAuthority = createAuthorityIfNotFound("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthorityIfNotFound("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthorityIfNotFound("DELETE_AUTHORITY");

        // 2. Create Roles and Admin user
        // use mutable lists: Hibernate's merge clears/replaces the collection in place
        createRoleIfNotFound("ROLE_ADMIN", new ArrayList<>(List.of(readAuthority, writeAuthority, deleteAuthority)));
        createRoleIfNotFound("ROLE_USER", new ArrayList<>(List.of(readAuthority)));

    }

    @Transactional
    private void createRoleIfNotFound(String name, Collection<AuthorityEntity> authorities) {
        roleRepository.findByName(name).ifPresentOrElse(role -> {
            // Update logic: Ensure existing roles get new authorities if added to the code
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }, () -> {
            RoleEntity role = new RoleEntity();
            role.setName(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        });
    }

    @Transactional
    private AuthorityEntity createAuthorityIfNotFound(String name) {
        return authorityRepository.findByName(name).orElseGet(() -> {
            AuthorityEntity authority = new AuthorityEntity();
            authority.setName(name);
            authorityRepository.save(authority);
            return authority;
        });
    }

}

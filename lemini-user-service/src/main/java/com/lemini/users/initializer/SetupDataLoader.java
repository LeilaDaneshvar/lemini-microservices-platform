package com.lemini.users.initializer;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lemini.users.io.entity.AuthorityEntity;
import com.lemini.users.io.entity.RoleEntity;
import com.lemini.users.io.repository.AuthorityRepository;
import com.lemini.users.io.repository.RoleRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // 1. Create Authtorities
        AuthorityEntity readAuthority = createAuthorityIfNotFound("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthorityIfNotFound("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthorityIfNotFound("DELETE_AUTHORITY");

        // 2. Create Roles and Admin user
        createRoleIfNotFound("ROLE_ADMIN", List.of(readAuthority, writeAuthority, deleteAuthority));
        createRoleIfNotFound("ROLE_USER", List.of(readAuthority));

        alreadySetup = true;
    }

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

    private AuthorityEntity createAuthorityIfNotFound(String name) {
        return authorityRepository.findByName(name).orElseGet(() -> {
            AuthorityEntity authority = new AuthorityEntity();
            authority.setName(name);
            authorityRepository.save(authority);
            return authority;
        });
    }

}

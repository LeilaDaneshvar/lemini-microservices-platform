package com.lemini.users.io.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import com.lemini.users.io.entity.RoleEntity;


public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);

}

package com.lemini.users.io.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lemini.users.io.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> , PagingAndSortingRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);

}

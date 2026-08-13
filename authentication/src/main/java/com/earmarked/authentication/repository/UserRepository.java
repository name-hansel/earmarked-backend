package com.earmarked.authentication.repository;

import com.earmarked.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    boolean existsByUsername(String username);
}


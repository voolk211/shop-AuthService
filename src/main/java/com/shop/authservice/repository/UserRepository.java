package com.shop.authservice.repository;

import com.shop.authservice.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByUsername(String username);

    Optional<User> findByUsername(String username);

}

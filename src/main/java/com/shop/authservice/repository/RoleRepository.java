package com.shop.authservice.repository;

import com.shop.authservice.model.entities.Role;
import com.shop.authservice.model.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

}

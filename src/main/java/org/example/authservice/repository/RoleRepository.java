package org.example.authservice.repository;

import org.example.authservice.model.entities.Role;
import org.example.authservice.model.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

}

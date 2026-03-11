package com.shop.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import com.shop.authservice.model.dto.auth.RegistrationRequest;
import com.shop.authservice.model.entities.Role;
import com.shop.authservice.model.entities.RoleName;
import com.shop.authservice.model.entities.User;
import com.shop.authservice.repository.RoleRepository;
import com.shop.authservice.repository.UserRepository;
import com.shop.authservice.service.AuthCredentialService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthCredentialServiceImpl implements AuthCredentialService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User saveCredentials(RegistrationRequest registrationRequest){
        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));
        User user = new User();
        user.setRoles(Set.of(role));
        user.setUsername(registrationRequest.getName());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setActive(registrationRequest.getActive());
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCredentialsCompensate(Long userId) {
        try {
            userRepository.deleteById(userId);
        }
        catch (EmptyResultDataAccessException ignored) {
        }
    }

}

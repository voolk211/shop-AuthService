package com.shop.authservice.service;

import com.shop.authservice.model.dto.auth.RegistrationRequest;
import com.shop.authservice.model.entities.User;


public interface AuthCredentialService {

    User saveCredentials(RegistrationRequest registrationRequest);

    void deleteCredentialsCompensate(Long userId);

}

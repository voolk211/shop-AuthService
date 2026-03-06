package org.example.authservice.client;

import org.example.authservice.model.dto.user.CreateUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {

    @PostMapping("/api/internal/users")
    CreateUser createUser(@RequestBody CreateUser createUserRequest);

    @DeleteMapping("/api/internal/users/{id}")
    void deleteUser(@PathVariable Long id);

}

package com.shop.authservice.client;

import com.shop.authservice.model.dto.user.CreateUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {

    @PostMapping("/api/internal/users")
    CreateUser createUser(@RequestBody CreateUser createUserRequest);
}

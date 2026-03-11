package com.shop.authservice.model.dto.auth;

import lombok.Data;
import com.shop.authservice.model.entities.RoleName;

@Data
public class AddRoleRequest {
    private RoleName roleName;
}

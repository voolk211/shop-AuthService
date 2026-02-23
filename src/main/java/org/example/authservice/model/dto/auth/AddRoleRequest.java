package org.example.authservice.model.dto.auth;

import lombok.Data;
import org.example.authservice.model.entities.RoleName;

@Data
public class AddRoleRequest {
    private RoleName roleName;
}

package com.example.courseapplicationproject.dto.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @JsonProperty("role_name")
    String roleName;

    String description;
    Set<PermissionCreateRequest> permissions;
}

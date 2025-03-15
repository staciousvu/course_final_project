package com.example.courseapplicationproject.mapper;

import org.mapstruct.Mapper;

import com.example.courseapplicationproject.dto.request.RoleRequest;
import com.example.courseapplicationproject.dto.response.RoleResponse;
import com.example.courseapplicationproject.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse roleToRoleResponse(Role role);

    Role roleRequestToRole(RoleRequest roleRequest);
}

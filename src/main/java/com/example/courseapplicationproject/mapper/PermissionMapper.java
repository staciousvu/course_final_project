package com.example.courseapplicationproject.mapper;

import org.mapstruct.Mapper;

import com.example.courseapplicationproject.dto.request.PermissionCreateRequest;
import com.example.courseapplicationproject.dto.response.PermissionResponse;
import com.example.courseapplicationproject.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);

    Permission toPermission(PermissionCreateRequest permissionCreateRequest);
}

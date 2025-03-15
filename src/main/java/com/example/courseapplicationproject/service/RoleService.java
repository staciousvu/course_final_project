package com.example.courseapplicationproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.RoleRequest;
import com.example.courseapplicationproject.dto.response.RoleResponse;
import com.example.courseapplicationproject.entity.Role;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.PermissionMapper;
import com.example.courseapplicationproject.mapper.RoleMapper;
import com.example.courseapplicationproject.repository.RoleRepository;
import com.example.courseapplicationproject.service.interfaces.IRoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class RoleService implements IRoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionMapper permissionMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.existsByRoleName(roleRequest.getRoleName())) throw new AppException(ErrorCode.ROLE_EXISTED);
        Role role = Role.builder()
                .roleName(roleRequest.getRoleName())
                .description(roleRequest.getDescription())
                .permissions(roleRequest.getPermissions().stream()
                        .map(permissionMapper::toPermission)
                        .collect(Collectors.toSet()))
                .build();
        roleRepository.save(role);
        return roleMapper.roleToRoleResponse(role);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::roleToRoleResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public RoleResponse getRoleById(Integer id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return roleMapper.roleToRoleResponse(role);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public RoleResponse updateRole(Integer id, RoleRequest roleRequest) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        role.setRoleName(roleRequest.getRoleName());
        role.setDescription(roleRequest.getDescription());
        role.setPermissions(roleRequest.getPermissions().stream()
                .map(permissionMapper::toPermission)
                .collect(Collectors.toSet()));
        roleRepository.save(role);
        return roleMapper.roleToRoleResponse(role);
    }
}

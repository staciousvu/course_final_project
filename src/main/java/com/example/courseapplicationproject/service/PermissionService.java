package com.example.courseapplicationproject.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.PermissionCreateRequest;
import com.example.courseapplicationproject.dto.response.PermissionResponse;
import com.example.courseapplicationproject.entity.Permission;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.PermissionMapper;
import com.example.courseapplicationproject.repository.PermissionRepository;
import com.example.courseapplicationproject.service.interfaces.IPermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class PermissionService implements IPermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public PermissionResponse createPermission(PermissionCreateRequest permissionCreateRequest) {
        log.info("Create Permission");
        if (permissionRepository.existsByPermissionName(permissionCreateRequest.getPermissionName()))
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        Permission permission = Permission.builder()
                .permissionName(permissionCreateRequest.getPermissionName())
                .description(permissionCreateRequest.getDescription())
                .build();
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public void deletePermission(Integer permissionId) {
        Permission permission = permissionRepository
                .findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionRepository.delete(permission);
    }
}

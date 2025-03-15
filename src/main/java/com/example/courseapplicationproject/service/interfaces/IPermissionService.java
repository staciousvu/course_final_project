package com.example.courseapplicationproject.service.interfaces;

import java.util.List;

import com.example.courseapplicationproject.dto.request.PermissionCreateRequest;
import com.example.courseapplicationproject.dto.response.PermissionResponse;

public interface IPermissionService {
    PermissionResponse createPermission(PermissionCreateRequest permissionCreateRequest);

    List<PermissionResponse> getAllPermissions();

    void deletePermission(Integer permissionId);
}

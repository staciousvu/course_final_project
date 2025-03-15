package com.example.courseapplicationproject.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.request.PermissionCreateRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.PermissionResponse;
import com.example.courseapplicationproject.service.interfaces.IPermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/permissions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PermissionController {
    IPermissionService permissionService;

    @PostMapping("/create")
    public ApiResponse<PermissionResponse> createPermission(
            @Valid @RequestBody PermissionCreateRequest permissionCreateRequest) {
        return ApiResponse.success(
                permissionService.createPermission(permissionCreateRequest), "Create Permission Successful");
    }

    @GetMapping("")
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.success(permissionService.getAllPermissions(), "Get All Permissions Successful");
    }

    @DeleteMapping("/{permissionId}")
    public ApiResponse<Void> deletePermission(@PathVariable("permissionId") Integer permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResponse.success(null, "Delete Permission Successful");
    }
}

package com.example.courseapplicationproject.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.request.RoleRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.RoleResponse;
import com.example.courseapplicationproject.service.interfaces.IRoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/roles")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RoleController {
    IRoleService roleService;

    @PostMapping("/create")
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.success(roleService.createRole(roleRequest), "Role created successfully");
    }

    @GetMapping("")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles(), "Get all roles successfully");
    }

    @GetMapping("/{roleId}")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable("roleId") Integer roleId) {
        return ApiResponse.success(roleService.getRoleById(roleId), "Get role successfully");
    }

    @PutMapping("/update/{roleId}")
    public ApiResponse<RoleResponse> updateRole(
            @PathVariable("roleId") Integer roleId, @RequestBody RoleRequest roleRequest) {
        return ApiResponse.success(roleService.updateRole(roleId, roleRequest), "Role updated successfully");
    }
}

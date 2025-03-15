package com.example.courseapplicationproject.service.interfaces;

import java.util.List;

import com.example.courseapplicationproject.dto.request.RoleRequest;
import com.example.courseapplicationproject.dto.response.RoleResponse;

public interface IRoleService {
    public RoleResponse createRole(RoleRequest roleRequest);

    public List<RoleResponse> getAllRoles();

    public RoleResponse getRoleById(Integer id);

    public RoleResponse updateRole(Integer id, RoleRequest roleRequest);
}

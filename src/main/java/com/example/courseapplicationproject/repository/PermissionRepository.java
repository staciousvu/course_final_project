package com.example.courseapplicationproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    public boolean existsByPermissionName(String permissionName);
}

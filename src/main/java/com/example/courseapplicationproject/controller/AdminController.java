package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.AdminCreateDTO;
import com.example.courseapplicationproject.dto.request.AdminUpdateDTO;
import com.example.courseapplicationproject.dto.response.AdminDTO;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminController {
    AdminService adminService;
    @PostMapping("/add-admin")
    public ApiResponse<Void> addAdmin(@RequestBody AdminCreateDTO adminCreateDTO) {
        adminService.addAdmin(adminCreateDTO);
        return ApiResponse.success(null,"OK");
    }
    @GetMapping("/{id}")
    public ApiResponse<AdminUpdateDTO> getAdmin(@PathVariable long id) {
        return ApiResponse.success(adminService.getAdmin(id),"OK");
    }
    @GetMapping("/all")
    public ApiResponse<List<AdminDTO>> getAllAdmins(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminService.getAllAdmins(keyword),"OK");
    }
    @PutMapping("/edit/{adminId}")
    public ApiResponse<Void> updateAdmin(@PathVariable Long adminId, @RequestBody AdminUpdateDTO adminUpdateDTO) {
        adminService.updateAdmin(adminId, adminUpdateDTO);
        return ApiResponse.success(null,"OK");
    }
    @PostMapping("/upload-avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) {
        return ApiResponse.success(adminService.uploadAvatar(avatar), "OK");
    }
    @PutMapping("/{id}/block")
    public ApiResponse<Void> blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
        return ApiResponse.success(null,"OK");
    }
}

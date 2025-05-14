package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.HomeCategoryRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.HomeCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hc")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class HomeCategoryController {

    HomeCategoryService homeCategoryService;

    @PostMapping("/update")
    public ApiResponse<Void> updateHomeCategory(@RequestBody HomeCategoryRequest request) {
        Long categoryId = request.getCategoryId();
        boolean checked = request.isChecked();

        if (checked) {
            homeCategoryService.addHomeCategory(categoryId);
            return ApiResponse.success(null, "Thêm HomeCategory thành công");
        } else {
            homeCategoryService.removeHomeCategory(categoryId);
            return ApiResponse.success(null, "Xóa HomeCategory thành công");
        }
    }
    @GetMapping("/list")
    public ApiResponse<List<Long>> getAllHomeCategory() {
        return ApiResponse.success(homeCategoryService.getAllHomeCategoryId(),"OK");
    }
}


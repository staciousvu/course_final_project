package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.FavoriteResponse;
import com.example.courseapplicationproject.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FavoriteController {
    FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<FavoriteResponse> getFavoritesForUser() {
        return ApiResponse.success(favoriteService.getFavoritesForUser(), "Lấy danh sách khóa học yêu thích thành công");
    }

    @PostMapping("/{courseId}")
    public ApiResponse<Void> addCourseToFavorites(@PathVariable Long courseId) {
        favoriteService.addCourseToFavorites(courseId);
        return ApiResponse.success(null, "Thêm khóa học vào danh sách yêu thích thành công");
    }

    @DeleteMapping("/{courseId}")
    public ApiResponse<Void> removeCourseFromFavorites(@PathVariable Long courseId) {
        favoriteService.removeCourseFromFavorites(courseId);
        return ApiResponse.success(null, "Xóa khóa học khỏi danh sách yêu thích thành công");
    }
}

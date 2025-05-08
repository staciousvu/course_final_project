package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.SlideRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.entity.Slide;
import com.example.courseapplicationproject.service.SlideService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/slide")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SlideController {
    SlideService slideService;
    @PostMapping
    public ApiResponse<Slide> createSlide(@RequestParam("image") MultipartFile imageFile) {
        Slide slide = slideService.createSlide(imageFile);
        return ApiResponse.success(slide, "Tạo slide thành công");
    }
    @PostMapping("/network")
    public ApiResponse<Slide> createSlideUrlNetwork(@RequestBody SlideRequest slideRequest) {
        Slide slide = slideService.createSlideUrlNetwork(slideRequest.getUrlImage());
        return ApiResponse.success(slide, "Tạo slide net work thành công");
    }

    @PutMapping("/{id}/position")
    public ApiResponse<Slide> updateSlidePosition(@PathVariable Long id, @RequestParam Long newPosition) {
        Slide updatedSlide = slideService.updateSlidePosition(id, newPosition);
        return ApiResponse.success(updatedSlide, "Cập nhật vị trí thành công");
    }

    @GetMapping
    public ApiResponse<List<Slide>> getAllSlides() {
        List<Slide> slides = slideService.getAllSlidesSortedByPosition();
        return ApiResponse.success(slides, "Lấy danh sách slide thành công");
    }

    @PutMapping("/{id}/toggle-active")
    public ApiResponse<Slide> toggleSlideActive(@PathVariable Long id) {
        Slide slide = slideService.toggleSlideActive(id);
        return ApiResponse.success(slide, "Thay đổi trạng thái hoạt động thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSlide(@PathVariable Long id) {
        slideService.deleteSlide(id);
        return ApiResponse.success(null,"Xóa slide thành công");
    }
}

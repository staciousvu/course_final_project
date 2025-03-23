package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.SectionCreateRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.SectionResponse;
import com.example.courseapplicationproject.service.SectionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/section")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SectionController {
    SectionService sectionService;

    @PostMapping("/create")
    public ApiResponse<SectionResponse> createSection(@RequestBody SectionCreateRequest request) {
        SectionResponse response = sectionService.createSection(request);
        return ApiResponse.success(response, "Section created successfully");
    }

    @PutMapping("/edit/{sectionId}")
    public ApiResponse<SectionResponse> editSection(
            @PathVariable Long sectionId,
            @RequestBody SectionCreateRequest request) {
        SectionResponse response = sectionService.editSection(sectionId, request);
        return ApiResponse.success(response, "Section updated successfully");
    }

    @DeleteMapping("/delete/{sectionId}")
    public ApiResponse<Void> deleteSection(@PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);
        return ApiResponse.success(null, "Section deleted successfully");
    }
}

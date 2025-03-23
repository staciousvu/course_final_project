package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.LectureCreateRequest;
import com.example.courseapplicationproject.dto.request.LectureUploadRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.LectureResponse;
import com.example.courseapplicationproject.service.LectureService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/lecture")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class LectureController {
    LectureService lectureService;

    @PostMapping("/create")
    public ApiResponse<LectureResponse> createLecture(@RequestBody LectureCreateRequest request) {
        log.info("Received request to create lecture: {}", request.getTitle());
        LectureResponse response = lectureService.createLecture(request);
        return ApiResponse.success(response, "Lecture created successfully");
    }

    @PostMapping("/upload")
    public CompletableFuture<ApiResponse<LectureResponse>> uploadLecture(@ModelAttribute LectureUploadRequest request) {
        log.info("Received request to upload lecture video for ID: {}", request.getLectureId());
        return lectureService.uploadLecture(request)
                .thenApply(response -> ApiResponse.success(response, "Lecture uploaded successfully"));
    }

    @DeleteMapping("/{lectureId}")
    public ApiResponse<Void> deleteLecture(@PathVariable Long lectureId) {
        log.info("Received request to delete lecture with ID: {}", lectureId);
        lectureService.deleteLecture(lectureId);
        return ApiResponse.success(null, "Lecture deleted successfully");
    }
}

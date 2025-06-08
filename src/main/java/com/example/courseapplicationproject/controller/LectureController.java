package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.LectureCreateRequest;
import com.example.courseapplicationproject.dto.request.LectureUpdateRequest;
import com.example.courseapplicationproject.dto.request.LectureUploadDocumentRequest;
import com.example.courseapplicationproject.dto.request.LectureUploadVideoRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.LectureResponse;
import com.example.courseapplicationproject.service.LectureService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
    @PutMapping("/edit/{lectureId}")
    public ApiResponse<Void> updateLecture(@PathVariable Long lectureId,@RequestBody LectureUpdateRequest lectureUpdateRequest) {
        log.info("Received request to update lecture with id: {}", lectureId);
        lectureService.updateLecture(lectureId, lectureUpdateRequest.getTitle());
        return ApiResponse.success(null, "Lecture updated successfully");
    }
    @PutMapping("/edit-preview/{lectureId}")
    public ApiResponse<Void> updateLecture(@PathVariable Long lectureId) {
        log.info("Received request to update lecture with id: {}", lectureId);
        lectureService.updatePreviewableVideo(lectureId);
        return ApiResponse.success(null, "Lecture updated successfully");
    }


    @PostMapping("/upload-video")
    public ApiResponse<Void> uploadLectureVideo(@ModelAttribute LectureUploadVideoRequest request) throws ExecutionException, InterruptedException, IOException {
        log.info("Received request to upload lecture video for ID: {}", request.getLectureId());
        lectureService.uploadLectureVideo(request);
        return ApiResponse.success(null,"OK");
    }
    @PostMapping("/upload-document")
    public ApiResponse<String> uploadLectureDocument(@ModelAttribute LectureUploadDocumentRequest request) throws ExecutionException, InterruptedException, IOException {
        return ApiResponse.success(lectureService.uploadLectureDocument(request),"OK");
    }

    @DeleteMapping("/{lectureId}")
    public ApiResponse<Void> deleteLecture(@PathVariable Long lectureId) {
        log.info("Received request to delete lecture with ID: {}", lectureId);
        lectureService.deleteLecture(lectureId);
        return ApiResponse.success(null, "Lecture deleted successfully");
    }
}

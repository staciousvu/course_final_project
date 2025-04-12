package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.LectureProgressResponse;
import com.example.courseapplicationproject.dto.response.ProgressResponse;
import com.example.courseapplicationproject.service.ProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProgressController {
    ProgressService progressService;

//    @GetMapping("/{courseId}")
//    public ApiResponse<ProgressResponse> getProgressForCourse(@PathVariable Long courseId) {
//        ProgressResponse progressResponse = progressService.getProgressForCourse(courseId);
//        return ApiResponse.success(progressResponse, "Lấy tiến trình khóa học thành công");
//    }

    @PostMapping("/complete-lecture/{lectureId}")
    public ApiResponse<LectureProgressResponse> markLectureCompleted(@PathVariable Long lectureId) {
        LectureProgressResponse lectureProgressResponse = progressService.maskLectureCompleted(lectureId);
        return ApiResponse.success(lectureProgressResponse, "Đánh dấu bài giảng hoàn thành thành công");
    }
}

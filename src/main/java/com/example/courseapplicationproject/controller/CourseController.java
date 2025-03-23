package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CourseUpdateRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.dto.response.CourseSectionLectureResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/course")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CourseController {
    CourseService courseService;
    @PostMapping("/draft")
    public ApiResponse<Void> createDraftCourse(@RequestParam String title,@RequestParam Long categoryId) {
        courseService.createDraftCourse(title,categoryId);
        return ApiResponse.success(null, "OK");
    }

    @PutMapping("/{courseId}")
    public ApiResponse<Void> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseUpdateRequest request) {
        courseService.updateCourse(courseId, request);
        return ApiResponse.success(null, "OK");
    }

    @PostMapping("/{courseId}/thumbnail")
    public ApiResponse<String> uploadThumbnail(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile thumbnail) {
        String secureUrl = courseService.uploadThumbnail(courseId, thumbnail);
        return ApiResponse.success(secureUrl, "Thumbnail uploaded successfully");
    }

    @PostMapping("/{courseId}/preview-video")
    public ApiResponse<Void> uploadPreviewVideo(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile previewVideo) {
        courseService.uploadPreviewVideo(courseId, previewVideo);
        return ApiResponse.success(null, "Preview video uploaded successfully");
    }
    @GetMapping("/my-courses/learner")
    public ApiResponse<Page<CourseResponse>> myCoursesLearner(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CourseResponse> courses = courseService.myCoursesLearner(page, size);
        return ApiResponse.success(courses, "Learner courses retrieved successfully");
    }

    @GetMapping("/my-courses/instructor")
    public ApiResponse<List<CourseResponse>> myCoursesInstructor() {
        List<CourseResponse> courses = courseService.myCoursesInstructor();
        return ApiResponse.success(courses, "Instructor courses retrieved successfully");
    }

    @PostMapping("/{courseId}/submit")
    public ApiResponse<Void> submitCourseForApproval(@PathVariable Long courseId) {
        courseService.submitCourseForApproval(courseId);
        return ApiResponse.success(null, "Course submitted for approval");
    }

    @PostMapping("/{courseId}/accept")
    public ApiResponse<Void> acceptCourse(@PathVariable Long courseId) {
        courseService.acceptCourse(courseId);
        return ApiResponse.success(null, "Course accepted");
    }

    @PostMapping("/{courseId}/reject")
    public ApiResponse<Void> rejectCourse(
            @PathVariable Long courseId,
            @RequestParam String reason) {
        courseService.rejectCourse(courseId, reason);
        return ApiResponse.success(null, "Course rejected");
    }

    @DeleteMapping("/{courseId}")
    public ApiResponse<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ApiResponse.success(null, "Course deleted successfully");
    }
    @GetMapping("/{courseId}/sections-lectures")
    public ApiResponse<CourseSectionLectureResponse> getSectionLectureForCourse(@PathVariable Long courseId) {
        CourseSectionLectureResponse response = courseService.getSectionLectureForCourse(courseId);
        return ApiResponse.success(response, "Get section lecture for course");
    }
}

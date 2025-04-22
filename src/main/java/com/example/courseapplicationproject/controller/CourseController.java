package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CourseUpdateRequest;
import com.example.courseapplicationproject.dto.request.FilterRequest;
import com.example.courseapplicationproject.dto.response.*;
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
    @GetMapping("/courses/accepted")
    public ApiResponse<List<CourseResponse>> getAllCourseAccept(
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(required = false) List<String> sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CourseResponse> courses = courseService.getAllCourseAccept(sortBy, sortDirection, page, size);
        return ApiResponse.success(courses,"OK");
    }


    @PostMapping("/search")
    public ApiResponse<Page<CourseResponse>> searchCourses(
            @RequestBody FilterRequest filterRequest,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "200") Integer size) {
        Page<CourseResponse> courses = courseService.searchCourses(filterRequest, page, size);
        return ApiResponse.success(courses, "OK");
    }
    @GetMapping("/search/basic")
    public ApiResponse<Page<CourseResponse>> searchCoursesBasic(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "200") Integer size) {
        Page<CourseResponse> courses = courseService.searchCoursesBasic(keyword, page, size);
        return ApiResponse.success(courses, "OK");
    }
    @GetMapping("pending-courses")
    public ApiResponse<List<CourseResponse>> getPendingCourses() {
        return ApiResponse.success(courseService.getAllCoursePending(),"OK");
    }
    @GetMapping("accept-courses")
    public ApiResponse<List<CourseResponse>> getAcceptCourses() {
        return ApiResponse.success(courseService.getAllCourseAccept(),"OK");
    }
    @GetMapping("reject-courses")
    public ApiResponse<List<CourseResponse>> getRejectCourses() {
        return ApiResponse.success(courseService.getAllCourseReject(),"OK");
    }
    @GetMapping("draft-courses")
    public ApiResponse<List<CourseResponse>> getDraftCourses() {
        return ApiResponse.success(courseService.getAllCourseDraft(),"OK");
    }

    @PostMapping("/draft")
    public ApiResponse<Long> createDraftCourse(@RequestParam String title) {
        return ApiResponse.success(courseService.createDraftCourse(title), "OK");
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

    @PutMapping("/{courseId}/accept")
    public ApiResponse<Void> acceptCourse(@PathVariable Long courseId) {
        courseService.acceptCourse(courseId);
        return ApiResponse.success(null, "Course accepted");
    }

    @PutMapping("/{courseId}/reject")
    public ApiResponse<Void> rejectCourse(
            @PathVariable Long courseId) {
        courseService.rejectCourse(courseId);
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
    @GetMapping("/{courseId}/sections-lectures/no-auth")
    public ApiResponse<CourseSectionLectureResponse> getSectionLectureForCourseNoAuth(@PathVariable Long courseId) {
        CourseSectionLectureResponse response = courseService.getSectionLectureForCourseNotAuthenticated(courseId);
        return ApiResponse.success(response, "Get section lecture for course");
    }

    @GetMapping("/basicinfo/{courseId}")
    public ApiResponse<CourseDTO> getBasicInfo(@PathVariable Long courseId) {
        return ApiResponse.success(courseService.getBasicInfo(courseId),"OK");
    }
    @GetMapping("/course-detail/{courseId}")
    public ApiResponse<CourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {
        return ApiResponse.success(courseService.getCourseDetail(courseId),"OK");
    }
}

package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.DiscussionRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.DiscussionResponse;
import com.example.courseapplicationproject.service.DiscussionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discussions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DiscussionController {
    DiscussionService discussionService;

    @PostMapping
    public ApiResponse<DiscussionResponse> createDiscussion(@RequestBody DiscussionRequest discussionRequest) {
        DiscussionResponse discussionResponse = discussionService.createDiscussion(discussionRequest);
        return ApiResponse.success(discussionResponse, "Discussion created successfully.");
    }

    @GetMapping("/lecture/{lectureId}")
    public ApiResponse<Page<DiscussionResponse>> getDiscussionsByLecture(
            @PathVariable Long lectureId,
            @RequestParam(defaultValue = "most_recent") String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DiscussionResponse> discussions = discussionService.getDiscussionsByLecture(lectureId, sortBy, keyword, page, size);
        return ApiResponse.success(discussions, "Fetched discussions for lecture.");
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<Page<DiscussionResponse>> getDiscussionsByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "most_recent") String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DiscussionResponse> discussions = discussionService.getDiscussionsByCourse(courseId, sortBy, keyword, page, size);
        return ApiResponse.success(discussions, "Fetched discussions for course.");
    }

    @GetMapping
    public ApiResponse<Page<DiscussionResponse>> getAllDiscussions(
            @RequestParam(defaultValue = "most_recent") String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DiscussionResponse> discussions = discussionService.getAllDiscussions(sortBy, keyword, page, size);
        return ApiResponse.success(discussions, "Fetched all discussions.");
    }
}

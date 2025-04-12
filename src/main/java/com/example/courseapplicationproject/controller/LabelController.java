package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.LabelRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.LabelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/label")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LabelController {
    LabelService labelService;
    @PostMapping("/add/{labelName}")
    public ApiResponse<Void> labelCourses(@RequestBody LabelRequest labelRequest, @PathVariable String labelName) {
        labelService.labelForCourses(labelRequest,labelName);
        return ApiResponse.success(null,"OK");
    }
}

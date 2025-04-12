package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.PreferenceRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.PreferenceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preference")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PreferenceController {
    PreferenceService preferenceService;
    @PostMapping("/edit")
    public ApiResponse<Void> updatePreference(@RequestBody PreferenceRequest preferenceRequest){
        preferenceService.updateUserPreferences(preferenceRequest.getCategoryRootId(),preferenceRequest.getSubCategoryIds());
        return ApiResponse.success(null,"OK");
    }
}

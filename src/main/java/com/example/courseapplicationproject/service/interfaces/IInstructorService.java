package com.example.courseapplicationproject.service.interfaces;

import com.example.courseapplicationproject.dto.response.InfoInstructorByCourseResponse;

public interface IInstructorService {
    void becomeInstructor();

    InfoInstructorByCourseResponse getInfoInstructorByCourse(Long courseId);
}

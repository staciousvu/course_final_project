package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.LabelRequest;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class LabelService {
    CourseRepository courseRepository;
    public void labelForCourses(LabelRequest labelRequest, String labelName){
        List<Course> courses = courseRepository.findAllById(labelRequest.getIds());
        courses.forEach(course -> {
           course.setLabel(Course.Label.valueOf(labelName));
        });
        courseRepository.saveAll(courses);
    }
//    public


}

package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.CourseContent;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseContentRepository;
import com.example.courseapplicationproject.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CourseContentService {
    CourseContentRepository courseContentRepository;
    CourseRepository courseRepository;
    public List<CourseContentDTO> getAllContents(Long courseId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return courseContentRepository.findByCourseIdOrderByIdAsc(courseId).stream().map(
                courseContent -> {
                    CourseContentDTO courseContentDTO = new CourseContentDTO();
                    courseContentDTO.setId(courseContent.getId());
                    courseContentDTO.setTitle(courseContent.getTitle());
                    return courseContentDTO;
                }
        ).toList();
    }
    public void createContents(Long courseId, List<CourseContentDTO> contents) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<CourseContent> contentList = contents.stream().map(dto -> {
            CourseContent content = new CourseContent();
            content.setTitle(dto.getTitle());
            content.setCourse(course);
            return content;
        }).collect(Collectors.toList());

        courseContentRepository.saveAll(contentList);
    }

    public void updateContents(Long courseId, List<CourseContentDTO> contents) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<CourseContent> updated = contents.stream().map(dto -> {
            CourseContent content = courseContentRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Content not found"));
            content.setTitle(dto.getTitle());
            return content;
        }).collect(Collectors.toList());

        courseContentRepository.saveAll(updated);
    }
    public void deleteContent(Long contentId) {
        courseContentRepository.deleteById(contentId);
    }
}

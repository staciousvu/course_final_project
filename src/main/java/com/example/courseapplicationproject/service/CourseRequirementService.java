package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import com.example.courseapplicationproject.dto.request.CourseRequirementDTO;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.CourseContent;
import com.example.courseapplicationproject.entity.CourseRequirement;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseContentRepository;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.CourseRequirementRepository;
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
public class CourseRequirementService {
    CourseRequirementRepository courseRequirementRepository;
    CourseRepository courseRepository;
    public List<CourseRequirementDTO> getAllRequirements(Long courseId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return courseRequirementRepository.findAll(sort).stream()
                .filter(requirement -> requirement.getCourse().getId().equals(courseId))
                .map(
                courseContent -> {
                    CourseRequirementDTO courseRequirementDTO = new CourseRequirementDTO();
                    courseRequirementDTO.setId(courseContent.getId());
                    courseRequirementDTO.setTitle(courseContent.getTitle());
                    return courseRequirementDTO;
                }
        ).toList();
    }
    public void createRequirements(Long courseId, List<CourseRequirementDTO> requirements) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<CourseRequirement> contentList = requirements.stream().map(dto -> {
            CourseRequirement content = new CourseRequirement();
            content.setTitle(dto.getTitle());
            content.setCourse(course);
            return content;
        }).collect(Collectors.toList());

        courseRequirementRepository.saveAll(contentList);
    }

    public void updateRequirements(Long courseId, List<CourseRequirementDTO> requirements) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<CourseRequirement> updated = requirements.stream().map(dto -> {
            CourseRequirement content = courseRequirementRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Content not found"));
            content.setTitle(dto.getTitle());
            return content;
        }).collect(Collectors.toList());

        courseRequirementRepository.saveAll(updated);
    }
    public void deleteRequirement(Long contentId) {
        courseRequirementRepository.deleteById(contentId);
    }
}

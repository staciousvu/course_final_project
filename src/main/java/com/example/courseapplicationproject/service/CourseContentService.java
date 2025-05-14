package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import com.example.courseapplicationproject.dto.request.CourseRequirementDTO;
import com.example.courseapplicationproject.dto.request.CourseTargetDTO;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.CourseContent;
import com.example.courseapplicationproject.entity.CourseRequirement;
import com.example.courseapplicationproject.entity.CourseTarget;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseContentRepository;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.CourseRequirementRepository;
import com.example.courseapplicationproject.repository.CourseTargetRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CourseContentService {
    CourseContentRepository courseContentRepository;
    CourseRepository courseRepository;
    CourseRequirementRepository courseRequirementRepository;
    CourseTargetRepository courseTargetRepository;
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
    @Transactional
    public void createContentRequirementTarget(Long courseId,
                                         List<CourseContentDTO> contents,
                                         List<CourseRequirementDTO> requirements,
                                         List<CourseTargetDTO> targets) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        courseContentRepository.deleteAllByCourseId(courseId);
        courseRequirementRepository.deleteAllByCourseId(courseId);
        courseTargetRepository.deleteAllByCourseId(courseId);

        List<CourseContent> contentList1 = contents.stream().map(dto -> {
            CourseContent content = new CourseContent();
            content.setTitle(dto.getTitle());
            content.setCourse(course);
            return content;
        }).collect(Collectors.toList());

        List<CourseRequirement> contentList2 = requirements.stream().map(dto -> {
            CourseRequirement content = new CourseRequirement();
            content.setTitle(dto.getTitle());
            content.setCourse(course);
            return content;
        }).collect(Collectors.toList());

        List<CourseTarget> targetList = targets.stream().map(dto -> {
            CourseTarget target = new CourseTarget();
            target.setTitle(dto.getTitle());
            target.setCourse(course);
            return target;
        }).collect(Collectors.toList());

        courseRequirementRepository.saveAll(contentList2);
        courseContentRepository.saveAll(contentList1);
        courseTargetRepository.saveAll(targetList);

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

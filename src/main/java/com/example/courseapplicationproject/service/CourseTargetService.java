package com.example.courseapplicationproject.service;
import com.example.courseapplicationproject.dto.request.CourseTargetDTO;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.CourseTarget;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.CourseTargetRepository;
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
public class CourseTargetService {
    CourseTargetRepository courseTargetRepository;
    CourseRepository courseRepository;

    public List<CourseTargetDTO> getAllTargets(Long courseId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return courseTargetRepository.findAll(sort).stream()
                .filter(target -> target.getCourse().getId().equals(courseId))
                .map(target -> {
                    CourseTargetDTO dto = new CourseTargetDTO();
                    dto.setId(target.getId());
                    dto.setTitle(target.getTitle());
                    return dto;
                }).toList();
    }

    public void createTargets(Long courseId, List<CourseTargetDTO> targets) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<CourseTarget> targetList = targets.stream().map(dto -> {
            CourseTarget target = new CourseTarget();
            target.setTitle(dto.getTitle());
            target.setCourse(course);
            return target;
        }).collect(Collectors.toList());

        courseTargetRepository.saveAll(targetList);
    }

    public void updateTargets(Long courseId, List<CourseTargetDTO> targets) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<CourseTarget> updated = targets.stream().map(dto -> {
            CourseTarget target = courseTargetRepository.findById(dto.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.TARGET_NOT_FOUND));
            target.setTitle(dto.getTitle());
            return target;
        }).collect(Collectors.toList());

        courseTargetRepository.saveAll(updated);
    }

    public void deleteTarget(Long targetId) {
        courseTargetRepository.deleteById(targetId);
    }
}

package com.example.courseapplicationproject.service;

import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.SectionCreateRequest;
import com.example.courseapplicationproject.dto.response.SectionResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.Section;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.SectionMapper;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.SectionRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class SectionService {
    SectionMapper sectionMapper;
    UserRepository userRepository;
    CourseRepository courseRepository;
    SectionRepository sectionRepository;

    public SectionResponse createSection(SectionCreateRequest sectionCreateRequest) {
        Course course = courseRepository
                .findById(sectionCreateRequest.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Section section = Section.builder()
                .title(sectionCreateRequest.getTitle())
                .displayOrder(sectionCreateRequest.getDisplayOrder() == null ? 0 : sectionCreateRequest.getDisplayOrder())
                .description(sectionCreateRequest.getDescription() == null ? "" : sectionCreateRequest.getDescription())
                .course(course)
                .build();
        sectionRepository.save(section);
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .displayOrder(section.getDisplayOrder())
                .description(section.getDescription())
                .build();
    }

    public SectionResponse editSection(Long sectionId, SectionCreateRequest sectionCreateRequest) {
        Section section =
                sectionRepository.findById(sectionId).orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));
        sectionMapper.updateSection(section, sectionCreateRequest);
        sectionRepository.save(section);
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .displayOrder(section.getDisplayOrder())
                .description(section.getDescription())
                .build();
    }

    public void deleteSection(Long sectionId) {
        Section section =
                sectionRepository.findById(sectionId).orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));
        sectionRepository.delete(section);
    }
}

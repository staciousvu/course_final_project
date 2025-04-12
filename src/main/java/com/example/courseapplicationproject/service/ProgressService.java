package com.example.courseapplicationproject.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.LectureProgressResponse;
import com.example.courseapplicationproject.dto.response.ProgressResponse;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class ProgressService {
    ProgressRepository progressRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    LectureRepository lectureRepository;
    EnrollRepository enrollRepository;

    public ProgressResponse getProgressForCourse(Course course_z,Long userId) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        int totalLectures = course_z.getSections().stream()
                .mapToInt(section -> section.getLectures().size())
                .sum();
        if (totalLectures == 0) {
            return ProgressResponse.builder()
                    .courseName(course_z.getTitle())
                    .percentage(0.0)
                    .totalLectures(0)
                    .totalLecturesCompleted(0)
//                    .lecturesCompleted(Collections.emptyList())
                    .build();
        }
        List<Long> idsLecture = course_z.getSections().stream()
                .flatMap(section -> section.getLectures().stream().map(Lecture::getId))
                .toList();
        List<CourseProgress> listLecturesCompleted = progressRepository.findAllLectureCompleted(idsLecture,userId);
        int totalLecturesCompleted = listLecturesCompleted.size();
//        List<ProgressResponse.LecturesCompleted> lecturesCompleted = listLecturesCompleted.stream()
//                .map(courseProgress -> ProgressResponse.LecturesCompleted.builder()
//                        .lectureId(courseProgress.getLecture().getId())
//                        .lectureName(courseProgress.getLecture().getTitle())
//                        .build())
//                .toList();
        double percentage = (double) totalLecturesCompleted * 100 / totalLectures;

        return ProgressResponse.builder()
                .courseName(course_z.getTitle())
                .percentage(percentage)
                .totalLectures(totalLectures)
                .totalLecturesCompleted(totalLecturesCompleted)
//                .lecturesCompleted(lecturesCompleted)
                .build();
    }

    public LectureProgressResponse maskLectureCompleted(Long lectureId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Long userId = user.getId();
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));
        Course course = lecture.getSection().getCourse();
        Long courseId = course.getId();
        if (!enrollRepository.existsByCourseIdAndUserId(courseId, userId))
            throw new AppException(ErrorCode.ACCESS_DENIED);
        Optional<CourseProgress> progress = progressRepository.findByLectureIdAndUserId(lectureId, userId);
        if (progress.isPresent()) {
            CourseProgress courseProgress = progress.get();
            return LectureProgressResponse.builder()
                    .lectureId(lectureId)
                    .isCompleted(courseProgress.getIsCompleted())
                    .lectureName(courseProgress.getLecture().getTitle())
                    .build();
        } else {
            CourseProgress newLectureProgress = progressRepository.save(CourseProgress.builder()
                    .isCompleted(true)
                    .course(course)
                    .lecture(lecture)
                    .user(user)
                    .build());
            return LectureProgressResponse.builder()
                    .lectureId(lectureId)
                    .isCompleted(true)
                    .lectureName(newLectureProgress.getLecture().getTitle())
                    .build();
        }
    }
}

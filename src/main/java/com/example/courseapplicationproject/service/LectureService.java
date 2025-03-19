package com.example.courseapplicationproject.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.LectureCreateRequest;
import com.example.courseapplicationproject.dto.request.LectureUploadRequest;
import com.example.courseapplicationproject.dto.response.LectureResponse;
import com.example.courseapplicationproject.entity.Lecture;
import com.example.courseapplicationproject.entity.Section;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.LectureRepository;
import com.example.courseapplicationproject.repository.SectionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class LectureService {
    LectureRepository lectureRepository;
    SectionRepository sectionRepository;
    CloudinaryService cloudinaryService;

    public LectureResponse createLecture(LectureCreateRequest lectureCreateRequest) {
        Section section = sectionRepository
                .findById(lectureCreateRequest.getSectionId())
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));
        Lecture lecture = Lecture.builder()
                .title(lectureCreateRequest.getTitle())
                .displayOrder(
                        lectureCreateRequest.getDisplayOrder() != null ? lectureCreateRequest.getDisplayOrder() : 0)
                .section(section)
                .build();
        lectureRepository.save(lecture);
        return LectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .displayOrder(lecture.getDisplayOrder())
                .build();
    }

    public LectureResponse uploadLecture(LectureUploadRequest lectureUploadRequest)
            throws ExecutionException, InterruptedException {
        Long lectureId = lectureUploadRequest.getLectureId();
        Lecture lecture =
                lectureRepository.findById(lectureId).orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));
        CompletableFuture<Map> uploadFuture = cloudinaryService.uploadVideoAuto(lectureUploadRequest.getFile());
        Map objects = uploadFuture.get();
        Integer duration = (Integer) objects.get("duration");
        String contentUrl = objects.get("secure_url").toString();
        lecture.setDuration(duration);
        lecture.setContentUrl(contentUrl);
        lecture.setType(Lecture.LectureType.valueOf(lectureUploadRequest.getType()));
        lectureRepository.save(lecture);
        return LectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .displayOrder(lecture.getDisplayOrder())
                .type(lecture.getType().name())
                .contentUrl(contentUrl)
                .duration(duration)
                .build();
    }

    public void deleteLecture(Long lectureId) {
        Lecture lecture =
                lectureRepository.findById(lectureId).orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));

        lectureRepository.delete(lecture);
    }
}

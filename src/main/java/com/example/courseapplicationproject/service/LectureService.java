package com.example.courseapplicationproject.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class LectureService {
    LectureRepository lectureRepository;
    SectionRepository sectionRepository;
    CloudinaryService cloudinaryService;
    GoogleCloudService googleCloudService;

    @Transactional
    public LectureResponse createLecture(LectureCreateRequest request) {
        log.info("Creating lecture: {}", request.getTitle());
        Section section = sectionRepository
                .findById(request.getSectionId())
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));

        Lecture lecture = Lecture.builder()
                .title(request.getTitle())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .duration(0.0)
                .section(section)
                .build();

        Lecture newLecture =  lectureRepository.save(lecture);
        return mapToResponse(newLecture);
    }
    public void updateLecture(Long lectureId,String title){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));
        lecture.setTitle(title);
        lectureRepository.save(lecture);
    }

    public void uploadLecture(LectureUploadRequest request) throws ExecutionException, InterruptedException, IOException {
        log.info("Uploading lecture video for ID: {}", request.getLectureId());

        Lecture lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));

//        Map objects = cloudinaryService.uploadVideoAuto(request.getFile()).get();
        String contentUrl = googleCloudService.uploadFile(request.getFile().getOriginalFilename(),
                request.getFile().getBytes(), request.getFile().getContentType());
        lecture.setContentUrl(contentUrl);
        lecture.setDuration(10.0);
//        lecture.setContentUrl(objects.get("secure_url").toString());
//        lecture.setDuration((Double) objects.get("duration"));
        lecture.setType(Lecture.LectureType.VIDEO);
        lectureRepository.save(lecture);
//        return cloudinaryService.uploadVideoAuto(request.getFile())
//                .thenApplyAsync(objects -> {
//                    Double durationDouble = (Double) objects.get("duration"); // Lấy duration dưới dạng Double
//                    Integer duration = durationDouble != null ? durationDouble.intValue() : null; // Chuyển đổi sang Integer
//
//                    String contentUrl = objects.get("secure_url").toString();
//                    lecture.setDuration(duration);
//                    lecture.setContentUrl(contentUrl);
//                    lecture.setType(Lecture.LectureType.valueOf(request.getType()));
//
//                    lectureRepository.save(lecture);
//                    return mapToResponse(lecture);
//                });
    }

    @Transactional
    public void deleteLecture(Long lectureId) {
        log.info("Deleting lecture with ID: {}", lectureId);
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));

        lectureRepository.delete(lecture);
    }

    private LectureResponse mapToResponse(Lecture lecture) {
        return LectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .displayOrder(lecture.getDisplayOrder())
                .type(lecture.getType() != null ? lecture.getType().name() : null)
                .contentUrl(lecture.getContentUrl())
                .duration(lecture.getDuration())
                .build();
    }
}

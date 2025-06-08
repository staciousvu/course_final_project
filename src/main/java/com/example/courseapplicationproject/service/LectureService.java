package com.example.courseapplicationproject.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.example.courseapplicationproject.dto.request.LectureUploadDocumentRequest;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.google.cloud.videointelligence.v1.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.LectureCreateRequest;
import com.example.courseapplicationproject.dto.request.LectureUploadVideoRequest;
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
    private final CourseRepository courseRepository;

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
                .courseId(request.getCourseId())
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
    public double getVideoDurationFromGCS(String gcsUri) throws IOException {
        try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
            // Tạo request
            AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
                    .setInputUri(gcsUri)
                    .addFeatures(Feature.LABEL_DETECTION)
                    .build();

            // Gửi request không đồng bộ và đợi kết quả
            AnnotateVideoResponse response = client.annotateVideoAsync(request)
                    .get(300, TimeUnit.SECONDS);

            // Lấy kết quả
            VideoAnnotationResults results = response.getAnnotationResults(0);

            // Lấy thời lượng video (trong giây)
            return results.getSegmentLabelAnnotations(0).getSegments(0).getSegment().getEndTimeOffset().getSeconds();
        } catch (Exception e) {
            throw new IOException("Error getting video duration from GCS", e);
        }
    }


    public void uploadLectureVideo(LectureUploadVideoRequest request) throws ExecutionException, InterruptedException, IOException {
        log.info("Uploading lecture video for ID: {}", request.getLectureId());

        Lecture lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));

//        Map objects = cloudinaryService.uploadVideoAuto(request.getFile()).get();
        String contentUrl = googleCloudService.uploadFile(request.getFile().getOriginalFilename(),
                request.getFile().getBytes(), request.getFile().getContentType());
        Course course = lecture.getSection().getCourse();
        course.setDuration(course.getDuration() + request.getDuration());
        lecture.setContentUrl(contentUrl);
        lecture.setPreviewable(false);
//        double duration = getVideoDurationFromGCS(contentUrl);
        lecture.setDuration(request.getDuration());
//        lecture.setContentUrl(objects.get("secure_url").toString());
//        lecture.setDuration((Double) objects.get("duration"));
//        lecture.setType(Lecture.LectureType.VIDEO);
        courseRepository.save(course);
        lectureRepository.save(lecture);

    }
    public String uploadLectureDocument(LectureUploadDocumentRequest request) throws IOException {
        log.info("Uploading lecture document for ID: {}", request.getLectureId());

        Lecture lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));

        // Upload file lên GCS
        String contentUrl = googleCloudService.uploadFile(
                request.getFile().getOriginalFilename(),
                request.getFile().getBytes(),
                request.getFile().getContentType()
        );

        lecture.setDocumentUrl(contentUrl);
        lectureRepository.save(lecture);
        return contentUrl;
    }
    public void updatePreviewableVideo(Long lectureId){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));
        lecture.setPreviewable(!lecture.isPreviewable());
        lectureRepository.save(lecture);
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
                .contentUrl(lecture.getContentUrl())
                .duration(lecture.getDuration())
                .build();
    }
}

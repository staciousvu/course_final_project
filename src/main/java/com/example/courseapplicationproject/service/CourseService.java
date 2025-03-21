package com.example.courseapplicationproject.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseapplicationproject.dto.request.CourseCreateRequest;
import com.example.courseapplicationproject.dto.request.FilterRequest;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.dto.response.CourseSectionLectureResponse;
import com.example.courseapplicationproject.elasticsearch.repository.CourseElasticRepository;
import com.example.courseapplicationproject.elasticsearch.service.CourseElasticService;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.*;
import com.example.courseapplicationproject.specifications.CourseSpecification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CourseService {
    CourseMapper courseMapper;
    CloudinaryService cloudinaryService;
    CourseRepository courseRepository;
    UserRepository userRepository;
    EnrollRepository enrollRepository;
    CategoryRepository categoryRepository;
    CourseElasticRepository courseElasticRepository;
    CourseElasticService courseElasticService;
    ActivityService activityService;
public Map<Long, Double> getAverageRatings(List<Long> courseIds) {
    List<Object[]> results = courseRepository.findAverageRatingsForCourses(courseIds);
    return results.stream()
            .collect(Collectors.toMap(row -> (Long) row[0], row -> (Double) row[1]));
}

    public Map<Long, Integer> getCountRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.countRatingsForCourses(courseIds);
        return results.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
    }

    public void enrollCourse(Payment payment) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<PaymentDetails> paymentDetails = payment.getPaymentDetails();
        paymentDetails.forEach(paymentDetail -> {
            Long courseId = paymentDetail.getCourse().getId();
            if (enrollRepository.existsByCourseIdAndUserId(courseId, user.getId())) {
                throw new AppException(ErrorCode.COURSE_ALREADY_PURCHASED);
            }
        });
        List<Enrollment> enrollments = paymentDetails.stream()
                .map(paymentDetail -> {
                    return Enrollment.builder()
                            .course(paymentDetail.getCourse())
                            .user(user)
                            .build();
                })
                .toList();
        enrollRepository.saveAll(enrollments);
    }

    public void createCourse(
            CourseCreateRequest courseCreateRequest, MultipartFile thumbnail, MultipartFile previewVideo) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Category category = categoryRepository
                .findById(courseCreateRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Course course = courseMapper.toCourse(courseCreateRequest);
        course.setStatus(Course.CourseStatus.DRAFT);
        course.setAuthor(user);
        course.setCategory(category);
        if (!thumbnail.isEmpty()) {
            Map objects = cloudinaryService.uploadImage(thumbnail);
            String secureUrl = objects.get("secure_url").toString();
            course.setThumbnail(secureUrl);
        }
        if (!previewVideo.isEmpty()) {
            try {
                Map objects = cloudinaryService.uploadVideoAuto(previewVideo).get();
                String secureUrl = objects.get("secure_url").toString();
                course.setPreviewVideo(secureUrl);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error uploading video", e);
            }
        }
        courseRepository.save(course);
    }

    public Page<CourseResponse> searchCourses(FilterRequest filterRequest, Integer page, Integer size) {
        String keyword = filterRequest.getKeyword();
        String language = filterRequest.getLanguage();
        String level = filterRequest.getLevel();
        Long categoryId = filterRequest.getCategoryId();
        Boolean isFree = filterRequest.getIsFree();
        Integer minDuration = filterRequest.getMinDuration();
        Integer maxDuration = filterRequest.getMaxDuration();
        Integer avgRatings = filterRequest.getAvgRatings();
        Boolean isAccepted = filterRequest.getIsAccepted();
        String sortBy = filterRequest.getSortBy();
        String sortDirection = filterRequest.getSortDirection();

        Specification<Course> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty()) {
            List<String> courseIdsString = courseElasticService.fuzzySearch(keyword);
            List<Long> courseIds = courseIdsString.stream().map(Long::parseLong).toList();
            if (courseIds.isEmpty()) return Page.empty();
            spec = spec.and(((root, query, criteriaBuilder) -> root.get("id").in(courseIds)));
        }
        spec = spec.and(CourseSpecification.hasCategory(categoryId))
                .and(CourseSpecification.hasAccepted(isAccepted))
                .and(CourseSpecification.hasLanguage(language))
                .and(CourseSpecification.isFree(isFree))
                .and(CourseSpecification.hasLevel(level))
                .and(CourseSpecification.hasDuration(minDuration, maxDuration));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Course> coursesPage = courseRepository.findAll(spec, pageRequest);
        List<Long> courseIds =
                coursesPage.getContent().stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);
        if (avgRatings != null) {
            List<Course> list = coursesPage
                    .filter(course -> {
                        double avgRating = avgRatingForCourses.getOrDefault(course.getId(), 0.0);
                        return avgRating >= avgRatings;
                    })
                    .toList();
            coursesPage = new PageImpl<>(list, pageRequest, list.size());
        }
        return getCourseResponses(coursesPage, avgRatingForCourses, countRatingForCourses);
    }

    private Page<CourseResponse> getCourseResponses(
            Page<Course> coursesPage, Map<Long, Double> avgRatingForCourses, Map<Long, Integer> countRatingForCourses) {
        return coursesPage.map(course -> {
            CourseResponse courseResponse = courseMapper.toCourseResponse(course);
            courseResponse.setStatus(course.getStatus().name());
            courseResponse.setLevel(course.getLevel().name());
            courseResponse.setCountRating(countRatingForCourses.getOrDefault(course.getId(), 0));
            courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
            courseResponse.setAuthorName(
                    course.getAuthor().getLastName() + " " + course.getAuthor().getFirstName());
            return courseResponse;
        });
    }

    public Page<CourseResponse> myCoursesLearner(Integer page, Integer size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course> coursesPage = courseRepository.findCoursesForUser(user.getId(), pageRequest);
        List<Long> courseIds =
                coursesPage.getContent().stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

        return getCourseResponses(coursesPage, avgRatingForCourses, countRatingForCourses);
    }

    public List<CourseResponse> myCoursesInstructor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Course> courses = courseRepository.findCourseByAuthorId(user.getId());
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(countRatingForCourses.getOrDefault(course.getId(), 0));
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " "
                            + course.getAuthor().getFirstName());
                    return courseResponse;
                })
                .collect(Collectors.toList());
    }

    public void submitCourseForApproval(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getAuthor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (!(course.getStatus().equals(Course.CourseStatus.DRAFT)
                || course.getStatus().equals(Course.CourseStatus.REJECTED))) {
            throw new AppException(ErrorCode.COURSE_CANNOT_BE_SUBMITTED);
        }
        course.setStatus(Course.CourseStatus.PENDING);
        courseRepository.save(course);
    }

    public void acceptCourse(Long courseId) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getStatus().equals(Course.CourseStatus.PENDING)) {
            throw new AppException(ErrorCode.COURSE_CANNOT_BE_ACCEPTED);
        }

        course.setStatus(Course.CourseStatus.ACCEPTED);
        courseRepository.save(course);
    }

    public void rejectCourse(Long courseId, String reason) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getStatus().equals(Course.CourseStatus.PENDING)) {
            throw new AppException(ErrorCode.COURSE_CANNOT_BE_REJECTED);
        }

        course.setStatus(Course.CourseStatus.REJECTED);

        courseRepository.save(course);
    }

    public void deleteCourse(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean isOwner = course.getAuthor().getId().equals(user.getId());
        boolean isAdmin =
                user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(Role.RoleType.ADMIN.toString()));

        if (!isOwner && !isAdmin) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (!(course.getStatus().equals(Course.CourseStatus.DRAFT)
                || course.getStatus().equals(Course.CourseStatus.PENDING)
                || course.getStatus().equals(Course.CourseStatus.REJECTED))) {
            throw new AppException(ErrorCode.COURSE_CANNOT_BE_DELETED);
        }

        courseRepository.delete(course);
    }

    public void editCourse(
            Long courseId,
            CourseCreateRequest courseCreateRequest,
            MultipartFile thumbnail,
            MultipartFile previewVideo) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Category category = categoryRepository
                .findById(courseCreateRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        courseMapper.updateCourse(course, courseCreateRequest);
        course.setStatus(Course.CourseStatus.DRAFT);
        course.setAuthor(user);
        course.setCategory(category);
        if (!thumbnail.isEmpty()) {
            Map objects = cloudinaryService.uploadImage(thumbnail);
            String secureUrl = objects.get("secure_url").toString();
            course.setThumbnail(secureUrl);
        }
        if (!previewVideo.isEmpty()) {
            try {
                Map objects = cloudinaryService.uploadVideoAuto(previewVideo).get();
                String secureUrl = objects.get("secure_url").toString();
                course.setPreviewVideo(secureUrl);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error uploading video", e);
            }
        }
        courseRepository.save(course);
    }

    public CourseSectionLectureResponse getSectionLectureForCourse(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean isEnrolled = enrollRepository.existsByCourseIdAndUserId(courseId, user.getId());
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        if (!isEnrolled)
            activityService.saveActivity(user,course);
        return CourseSectionLectureResponse.builder()
                .courseId(courseId)
                .totalSections(course.getSections().size())
                .totalLectures(course.getSections().stream()
                        .mapToInt(section -> section.getLectures().size())
                        .sum())
                .duration(course.getDuration())
                .sections(course.getSections().stream()
                        .sorted(Comparator.comparing(Section::getId))
                        .map(section -> CourseSectionLectureResponse.SectionResponse.builder()
                                .id(section.getId())
                                .title(section.getTitle())
                                .displayOrder(section.getDisplayOrder())
                                .totalLectures(section.getLectures().size())
                                .description(section.getDescription())
                                .lectures(section.getLectures().stream()
                                        .sorted(Comparator.comparing(Lecture::getId))
                                        .map(lecture -> CourseSectionLectureResponse.LectureResponse.builder()
                                                .id(lecture.getId())
                                                .title(lecture.getTitle())
                                                .displayOrder(lecture.getDisplayOrder())
                                                .type(lecture.getType().name())
                                                .contentUrl(isEnrolled ? lecture.getContentUrl() : null)
                                                .duration(lecture.getDuration())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

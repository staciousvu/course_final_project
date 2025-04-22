package com.example.courseapplicationproject.service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.*;
import com.example.courseapplicationproject.util.UserUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    VoucherService voucherService;
    ProgressRepository progressRepository;
    CourseContentService courseContentService;
    CourseRequirementService courseRequirementService;
    CourseTargetService courseTargetService;

    public Map<Long, Double> getAverageRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.findAverageRatingsForCourses(courseIds);
        return results.stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Double) row[1]));
    }

    public Map<Long, Integer> getCountRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.countRatingsForCourses(courseIds);
        return results.stream().collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
    }

    @Transactional
    public void enrollCourse(Payment payment) {
        User user = payment.getUser();
        List<PaymentDetails> paymentDetails = payment.getPaymentDetails();
        List<Enrollment> enrollments = paymentDetails.stream()
                .map(paymentDetail -> {
                    return Enrollment.builder()
                            .course(paymentDetail.getCourse())
                            .user(user)
                            .build();
                })
                .toList();
        enrollRepository.saveAll(enrollments);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        List<Long> courseIds = paymentDetails.stream()
                .map(paymentDetail -> paymentDetail.getCourse().getId())
                .toList();

        cartItemRepository.deleteByCartAndCourseIdIn(cart, courseIds);
    }
    public CourseDetailResponse getCourseDetail(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_FOUND));
        User user = course.getAuthor();
        CourseDetailResponse.AuthorDTO authorDTO = CourseDetailResponse.AuthorDTO
                .builder()
                .id(user.getId())
                .authorAvatar(user.getAvatar())
                .authorName(user.getFirstName() + " " + user.getLastName())
                .bio(user.getBio())
                .expertise(user.getExpertise())
                .build();
        Category topicCategory = course.getCategory();
        Category subCategory = topicCategory.getParentCategory();
        Category rootCategory = subCategory.getParentCategory();
        List<CourseDetailResponse.CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryDTOS.add(CourseDetailResponse.CategoryDTO.builder()
                        .id(rootCategory.getId())
                        .categoryName(rootCategory.getName())
                .build());
        categoryDTOS.add(CourseDetailResponse.CategoryDTO.builder()
                .id(subCategory.getId())
                .categoryName(subCategory.getName())
                .build());
        categoryDTOS.add(CourseDetailResponse.CategoryDTO.builder()
                .id(topicCategory.getId())
                .categoryName(topicCategory.getName())
                .build());
        List<CourseContentDTO> courseContentDTOS = courseContentService.getAllContents(courseId);
        List<CourseRequirementDTO> courseRequirementDTOS = courseRequirementService.getAllRequirements(courseId);
        List<CourseTargetDTO> courseTargetDTOS = courseTargetService.getAllTargets(courseId);
        return CourseDetailResponse.builder()
                .id(courseId)
                .title(course.getTitle())
                .subtitle(course.getSubtitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .discount_price(voucherService.calculateDiscountedPrice(course.getPrice()))
                .duration(course.getDuration())
                .language(course.getLanguage())
                .level(course.getLevel().name())
                .thumbnail(course.getThumbnail())
                .previewVideo(course.getPreviewVideo())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .author(authorDTO)
                .categories(categoryDTOS)
                .avgRating(courseRepository.findAverageRatingByCourseId(courseId))
                .countRating(course.getCountRating())
                .status(course.getStatus().name())
                .countEnrolled(course.getCountEnrolled())
                .label(course.getLabel().name())
                .contents(courseContentDTOS)
                .requirements(courseRequirementDTOS)
                .targets(courseTargetDTOS)
                .build();
    }


    public Long createDraftCourse(String title) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        Course course = Course.builder()
                .title(title)
                .status(UserUtils.isAdmin(user) ? Course.CourseStatus.PENDING : Course.CourseStatus.DRAFT)
                .author(user)
                .label(Course.Label.NONE)
                .level(Course.LevelCourse.BEGINNER)
                .isActive(Course.IsActive.ACTIVE)
                .countEnrolled(0)
                .countRating(0)
                .duration(0.0)
                .build();

        courseRepository.save(course);
        return course.getId();
    }

    public CourseDTO getBasicInfo(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .price(course.getPrice())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .subtitle(course.getSubtitle())
                .language(course.getLanguage())
                .description(course.getDescription())
                .level(course.getLevel().name())
                .previewUrl(course.getThumbnail())
                .videoUrl(course.getPreviewVideo())
                .avgRating(courseRepository.findAverageRatingByCourseId(courseId))
                .duration(course.getDuration())
                .countEnrolled(course.getCountEnrolled())
                .countRating(course.getCountRating())
                .build();
    }


    public void updateCourse(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!course.getAuthor().getEmail().equals(email) && !UserUtils.isAdmin(user)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            course.setCategory(category);
        }
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getSubtitle() != null) course.setSubtitle(request.getSubtitle());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
//        if (request.getDuration() != null) course.setDuration(request.getDuration());
        if (request.getLanguage() != null) course.setLanguage(request.getLanguage());
        if (request.getLevel() != null)
            course.setLevel(Course.LevelCourse.valueOf(request.getLevel()));

        courseRepository.save(course);
    }
    public String uploadThumbnail(Long courseId, MultipartFile thumbnail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!course.getAuthor().getEmail().equals(email) && !UserUtils.isAdmin(user)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        Map objects = cloudinaryService.uploadImage(thumbnail);
        String secureUrl = objects.get("secure_url").toString();
        course.setThumbnail(secureUrl);
        courseRepository.save(course);

        return secureUrl;
    }
    public void uploadPreviewVideo(Long courseId, MultipartFile previewVideo) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        if (!course.getAuthor().getEmail().equals(email) && !UserUtils.isAdmin(user)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        try {
            Map objects = cloudinaryService.uploadVideoAuto(previewVideo).get();
            course.setPreviewVideo(objects.get("secure_url").toString());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error uploading video", e);
        }
        courseRepository.save(course);
    }
    SearchHistoryRepository searchHistoryRepository;
    public Page<CourseResponse> searchCourses(FilterRequest filterRequest, Integer page, Integer size) {
        String keyword = filterRequest.getKeyword();
        List<String> languages = filterRequest.getLanguages();
        String level = filterRequest.getLevel();
        Long categoryId = filterRequest.getCategoryId();
        Boolean isFree = filterRequest.getIsFree();
        Integer minDuration = filterRequest.getMinDuration();
        Integer maxDuration = filterRequest.getMaxDuration();
        Integer avgRatings = filterRequest.getAvgRatings();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user=null;
        if (!email.isEmpty()){
            user= userRepository.findByEmail(email).orElse(null);
        }

        // Đảm bảo không bị null
        List<String> sortByList = Optional.ofNullable(filterRequest.getSortByList()).orElse(new ArrayList<>());
        List<String> sortDirectionList = Optional.ofNullable(filterRequest.getSortDirectionList()).orElse(new ArrayList<>());

        Specification<Course> spec = Specification.where(null);

        // Xử lý tìm kiếm theo keyword qua ElasticSearch
        if (keyword != null && !keyword.isEmpty()) {
            List<String> courseIdsString = courseElasticService.fuzzySearch(keyword);
            if (user!=null){
                UserSearchKeywordHistory userSearchKeywordHistory = UserSearchKeywordHistory.builder()
                        .keyword(keyword)
                        .user(user)
                        .build();
                searchHistoryRepository.save(userSearchKeywordHistory);
            }

            List<Long> courseIds = courseIdsString.stream().map(Long::parseLong).toList();
            if (courseIds.isEmpty()) return Page.empty();
            spec = spec.and(((root, query, criteriaBuilder) -> root.get("id").in(courseIds)));
        }

        spec = spec.and(CourseSpecification.hasCategory(categoryId))
                .and(CourseSpecification.hasLanguages(languages))
                .and(CourseSpecification.isActiveStatus(true))
                .and(CourseSpecification.isFree(isFree))
                .and(CourseSpecification.hasLevel(level))
                .and(CourseSpecification.hasDuration(minDuration, maxDuration));

        // Tạo danh sách Sort từ danh sách người dùng truyền lên
        List<Sort.Order> orders = new ArrayList<>();
        if (!sortByList.isEmpty() && sortByList.size() == sortDirectionList.size()) {
            for (int i = 0; i < sortByList.size(); i++) {
                String sortField = sortByList.get(i);
                String direction = sortDirectionList.get(i).toUpperCase();
                if (!"avgRating".equals(sortField)) { // avgRating sẽ được xử lý sau
                    orders.add(new Sort.Order(Sort.Direction.valueOf(direction), sortField));
                }
            }
        }

        Sort sort = orders.isEmpty() ? Sort.by("id").ascending() : Sort.by(orders);

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Course> coursesPage = courseRepository.findAll(spec, pageRequest);

        List<Long> courseIds = coursesPage.getContent().stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);

        // Lọc theo avgRatings nếu có
        if (avgRatings != null) {
            List<Course> filteredList = coursesPage.getContent().stream()
                    .filter(course -> avgRatingForCourses.getOrDefault(course.getId(), 0.0) >= avgRatings)
                    .toList();

            coursesPage = new PageImpl<>(filteredList, pageRequest, filteredList.size());
        }

        // Nếu danh sách có chứa avgRating, cần sắp xếp thủ công
        if (sortByList.contains("avgRating")) {
            Comparator<Course> comparator = Comparator.comparing(c -> avgRatingForCourses.getOrDefault(c.getId(), 0.0));

            for (int i = 0; i < sortByList.size(); i++) {
                if ("avgRating".equals(sortByList.get(i)) && "DESC".equalsIgnoreCase(sortDirectionList.get(i))) {
                    comparator = comparator.reversed();
                }
            }

            List<Course> sortedCourses = coursesPage.getContent().stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());

            coursesPage = new PageImpl<>(sortedCourses, pageRequest, coursesPage.getTotalElements());
        }

        return getCourseResponses(coursesPage, avgRatingForCourses);
    }
    public Page<CourseResponse> searchCoursesBasic(String keyword, Integer page, Integer size) {
        Specification<Course> spec = Specification.where(null);
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<String> courseIdsString = courseElasticService.fuzzySearch(keyword);
            List<Long> courseIds = courseIdsString.stream().map(Long::parseLong).toList();

            if (courseIds.isEmpty()) {
                return Page.empty();
            }
            spec = spec.and((root, query, cb) -> root.get("id").in(courseIds));
        }


        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course> coursesPage = courseRepository.findAll(spec, pageRequest);

        List<Long> ids = coursesPage.getContent().stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(ids);

        return getCourseResponses(coursesPage, avgRatingForCourses);
    }

//    public Page<CourseResponse> searchCourses(FilterRequest filterRequest, Integer page, Integer size) {
//        String keyword = filterRequest.getKeyword();
//        String language = filterRequest.getLanguage();
//        String level = filterRequest.getLevel();
//        Long categoryId = filterRequest.getCategoryId();
//        Boolean isFree = filterRequest.getIsFree();
//        Integer minDuration = filterRequest.getMinDuration();
//        Integer maxDuration = filterRequest.getMaxDuration();
//        Integer avgRatings = filterRequest.getAvgRatings();
//        Boolean isAccepted = filterRequest.getIsAccepted();
//        String sortBy = filterRequest.getSortBy() != null ? filterRequest.getSortBy() : "id";
//        String sortDirection = filterRequest.getSortDirection() != null ? filterRequest.getSortDirection().toUpperCase() : "ASC";
//
//        Specification<Course> spec = Specification.where(null);
//        if (keyword != null && !keyword.isEmpty()) {
//            List<String> courseIdsString = courseElasticService.fuzzySearch(keyword);
//            List<Long> courseIds = courseIdsString.stream().map(Long::parseLong).toList();
//            if (courseIds.isEmpty()) return Page.empty();
//            spec = spec.and(((root, query, criteriaBuilder) -> root.get("id").in(courseIds)));
//        }
//        spec = spec.and(CourseSpecification.hasCategory(categoryId))
//                .and(CourseSpecification.hasAccepted(isAccepted))
//                .and(CourseSpecification.hasLanguage(language))
//                .and(CourseSpecification.isFree(isFree))
//                .and(CourseSpecification.hasLevel(level))
//                .and(CourseSpecification.hasDuration(minDuration, maxDuration));
//        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection), sortBy);
//        PageRequest pageRequest = PageRequest.of(page,size,sort);
//        Page<Course> coursesPage = courseRepository.findAll(spec,pageRequest);
//        List<Long> courseIds =
//                coursesPage.getContent().stream().map(Course::getId).toList();
//        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
//        if (avgRatings != null) {
//            List<Course> list = coursesPage
//                    .filter(course -> {
//                        double avgRating = avgRatingForCourses.getOrDefault(course.getId(), 0.0);
//                        return avgRating >= avgRatings;
//                    })
//                    .toList();
//            coursesPage = new PageImpl<>(list, pageRequest, list.size());
//        }
//        return getCourseResponses(coursesPage, avgRatingForCourses);
//    }

    private Page<CourseResponse> getCourseResponses(
            Page<Course> coursesPage, Map<Long, Double> avgRatingForCourses) {
        return coursesPage.map(course -> {
            CourseResponse courseResponse = courseMapper.toCourseResponse(course);
            courseResponse.setStatus(course.getStatus().name());
            courseResponse.setLevel(course.getLevel().name());
            courseResponse.setLabel(course.getLabel().name());
            courseResponse.setCountRating(course.getCountRating());
            courseResponse.setCountEnrolled(course.getCountEnrolled());
            courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
            courseResponse.setAuthorName(
                    course.getAuthor().getLastName() + " " + course.getAuthor().getFirstName());
            courseResponse.setAuthorAvatar(course.getAuthor().getAvatar());
            courseResponse.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
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


        return getCourseResponses(coursesPage, avgRatingForCourses);
    }

    public List<CourseResponse> myCoursesInstructor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Course> courses = courseRepository.findCourseByAuthorId(user.getId());
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setLabel(course.getLabel().name());
                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
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

    public void rejectCourse(Long courseId) {
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
    public CourseSectionLectureResponse getSectionLectureForCourseNotAuthenticated(Long courseId) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return CourseSectionLectureResponse.builder()
                .courseName(course.getTitle())
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
                                                .isCompleted(false)
                                                .type(lecture.getType() != null ? lecture.getType().name() : null)
                                                .contentUrl( null)
                                                .duration(lecture.getDuration() != null ? lecture.getDuration() : 0)
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
    ProgressService progressService;
    public CourseSectionLectureResponse getSectionLectureForCourse(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean isEnrolled = enrollRepository.existsByCourseIdAndUserId(courseId, user.getId());
        boolean isAdmin = UserUtils.isAdmin(user);
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        ProgressResponse progressResponse = progressService.getProgressForCourse(course,user.getId());
        List<Object[]> progressList = progressRepository.findLectureProgressByCourseIdAndUserId(courseId, user.getId());
        Map<Long, Boolean> progressMap = progressList.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Boolean) row[1]));

        if (!isEnrolled) activityService.saveActivity(user, course);
        return CourseSectionLectureResponse.builder()
                .progressResponse(progressResponse)
                .courseName(course.getTitle())
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
                                                .isCompleted(progressMap.getOrDefault(lecture.getId(), false))
                                                .type(lecture.getType() != null ? lecture.getType().name() : null)
                                                .contentUrl((isEnrolled || isAdmin) ? lecture.getContentUrl() : null)
                                                .duration(lecture.getDuration() != null ? lecture.getDuration() : 0)
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
    public List<CourseResponse> getAllCoursePending(){
        Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");
        List<Course> courses = courseRepository.findCourseByStatus(Course.CourseStatus.PENDING,sort);
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setLabel(course.getLabel().name());
                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " "
                            + course.getAuthor().getFirstName());
                    return courseResponse;
                })
                .collect(Collectors.toList());
    }
    public List<CourseResponse> getAllCourseAccept(){
        Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");
        List<Course> courses = courseRepository.findCourseByStatus(Course.CourseStatus.ACCEPTED,sort);
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setLabel(course.getLabel().name());
                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " "
                            + course.getAuthor().getFirstName());
                    return courseResponse;
                })
                .collect(Collectors.toList());
    }
    public List<CourseResponse> getAllCourseReject(){
        Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");
        List<Course> courses = courseRepository.findCourseByStatus(Course.CourseStatus.REJECTED,sort);
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setLabel(course.getLabel().name());
                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " "
                            + course.getAuthor().getFirstName());
                    return courseResponse;
                })
                .collect(Collectors.toList());
    }
    public List<CourseResponse> getAllCourseDraft(){
        Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");
        List<Course> courses = courseRepository.findCourseByStatus(Course.CourseStatus.DRAFT,sort);
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setLabel(course.getLabel().name());
                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " "
                            + course.getAuthor().getFirstName());
                    return courseResponse;
                })
                .collect(Collectors.toList());
    }
    public List<CourseResponse> getAllCourseAccept(List<String> sortBy, List<String> sortDirection, int page, int size) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = List.of("createdAt"); // Mặc định sắp xếp theo createdAt
        }

        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = List.of("asc"); // Mặc định là asc
        }

        // Kiểm tra nếu có avgRating trong danh sách sortBy => cần sort riêng
        boolean hasAvgRating = sortBy.contains("avgRating");

        // Tạo danh sách sort, loại bỏ avgRating vì nó không có trong entity
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortBy.size(); i++) {
            String field = sortBy.get(i);
            if (!field.equals("avgRating")) { // Không thêm avgRating vào query vì nó không có trong DB
                String directionStr = (i < sortDirection.size()) ? sortDirection.get(i) : "asc";
                Sort.Direction direction = directionStr.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, field));
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Course> coursePage = courseRepository.findByStatus(Course.CourseStatus.ACCEPTED, pageable);
        List<Course> courses = coursePage.getContent();

        // Lấy danh sách ID của các khóa học
        List<Long> courseIds = courses.stream().map(Course::getId).toList();

        // Lấy avgRating cho từng khóa học
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);

        // Chuyển đổi Course -> CourseResponse
        List<CourseResponse> courseResponses = courses.stream()
                .map(course -> {
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setLabel(course.getLabel().name());
                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " " + course.getAuthor().getFirstName());
                    return courseResponse;
                })
                .collect(Collectors.toList());

        // Nếu có yêu cầu sắp xếp theo avgRating, thực hiện sắp xếp trong Java
        if (hasAvgRating) {
            int index = sortBy.indexOf("avgRating");
            String directionStr = (index < sortDirection.size()) ? sortDirection.get(index) : "asc";
            boolean isDescending = directionStr.equalsIgnoreCase("desc");

            courseResponses.sort(Comparator.comparing(CourseResponse::getAvgRating,
                    isDescending ? Comparator.reverseOrder() : Comparator.naturalOrder()));
        }

        return courseResponses;
    }



}

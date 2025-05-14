package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.response.AdsApplyResponse;
import com.example.courseapplicationproject.dto.response.AdvertisementResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AdvertisementService {

    AdvertisementRepository advertisementRepository;
    AdPackageRepository adPackageRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    AdsApplyRepository adsApplyRepository;
    CourseMapper courseMapper;
    VoucherService voucherService;

    // 1. Người dùng mua quảng cáo
    public void createAdvertisement(Payment payment) {
        User user = payment.getUser();
        Long adPackageId = payment.getAdPackageId();

        AdPackage adPackage = adPackageRepository.findById(adPackageId)
                .orElseThrow(() -> new AppException(ErrorCode.AD_PACKAGE_NOT_FOUND));

        Advertisement advertisement = Advertisement.builder()
                .user(user)
                .adPackage(adPackage)
                .used(false)
                .build();

        advertisementRepository.save(advertisement);
    }

    // 2. Người dùng áp dụng quảng cáo cho khóa học
    public void applyAdvertisement(Long advertisementId, Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        if (advertisement.isUsed()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        if (!advertisement.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getAuthor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        AdsApply adsApply = AdsApply.builder()
                .advertisement(advertisement)
                .course(course)
                .status(AdsApply.ApplicationStatus.PENDING)
                .build();

        adsApplyRepository.save(adsApply);
    }

    // 3. Admin duyệt quảng cáo
    public void approveApply(Long applyId) {
        AdsApply adsApply = adsApplyRepository.findById(applyId)
                .orElseThrow(() -> new AppException(ErrorCode.ADS_APPLY_NOT_FOUND));

        if (!adsApply.getStatus().equals(AdsApply.ApplicationStatus.PENDING)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        Advertisement advertisement = adsApply.getAdvertisement();

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(advertisement.getAdPackage().getDurationDays());

        advertisement.setUsed(true);
        advertisement.setStartDate(startDate);
        advertisement.setEndDate(endDate);

        advertisementRepository.save(advertisement);

        adsApply.setStatus(AdsApply.ApplicationStatus.APPROVED);
        adsApply.setRejectionReason(null);

        adsApplyRepository.save(adsApply);
    }

    // 4. Admin từ chối quảng cáo
    public void rejectApply(Long applyId, String reason) {
        AdsApply adsApply = adsApplyRepository.findById(applyId)
                .orElseThrow(() -> new AppException(ErrorCode.ADS_APPLY_NOT_FOUND));

        if (!adsApply.getStatus().equals(AdsApply.ApplicationStatus.PENDING)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        adsApply.setStatus(AdsApply.ApplicationStatus.REJECTED);
        adsApply.setRejectionReason(reason);

//        return adsApplyRepository.save(adsApply);
        adsApplyRepository.deleteById(applyId);
    }

    // 5. Lấy danh sách quảng cáo đã mua nhưng chưa dùng
    public List<AdvertisementResponse> getUnusedAdvertisements() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Advertisement> ads = advertisementRepository.findByUserIdAndUsedFalse(user.getId());

        return ads.stream()
                .filter(ad -> {
                    // Lọc bỏ quảng cáo đã có AdsApply (dù ở trạng thái nào)
                    AdsApply apply = adsApplyRepository.findByAdvertisementId(ad.getId()).orElse(null);
                    return apply == null || apply.getStatus() == AdsApply.ApplicationStatus.REJECTED;
                })
                .map(ad -> {
                    AdsApply apply = adsApplyRepository.findByAdvertisementId(ad.getId()).orElse(null);

                    return AdvertisementResponse.builder()
                            .advertisementId(ad.getId())
                            .startDate(ad.getStartDate())
                            .endDate(ad.getEndDate())
                            .createdAt(ad.getCreatedAt())
                            .durationDays(ad.getAdPackage().getDurationDays())
                            .packageName(ad.getAdPackage().getName())
                            .courseId(apply != null ? apply.getCourse().getId() : null)
                            .courseTitle(apply != null ? apply.getCourse().getTitle() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 6. Lấy danh sách quảng cáo đang được sử dụng và chưa hết hạn
    public List<AdvertisementResponse> getActiveAdvertisements() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> ads = advertisementRepository.findByUserIdAndUsedTrueAndEndDateAfter(user.getId(), now);

        return ads.stream()
                .map(ad -> {
                    AdsApply apply = adsApplyRepository.findByAdvertisementId(ad.getId()).orElse(null);

                    return AdvertisementResponse.builder()
                            .advertisementId(ad.getId())
                            .startDate(ad.getStartDate())
                            .endDate(ad.getEndDate())
                            .createdAt(ad.getCreatedAt())
                            .durationDays(ad.getAdPackage().getDurationDays())
                            .packageName(ad.getAdPackage().getName())
                            .courseId(apply != null ? apply.getCourse().getId() : null)
                            .courseTitle(apply != null ? apply.getCourse().getTitle() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 7. Admin - Lấy danh sách các AdsApply đang chờ duyệt
    public List<AdsApplyResponse> getPendingAdsApplies() {
        List<AdsApply> applies = adsApplyRepository.findByStatus(AdsApply.ApplicationStatus.PENDING);

        return applies.stream()
                .map(apply -> {
                    Advertisement ad = apply.getAdvertisement();
                    Course course = apply.getCourse();
                    User author = course.getAuthor();

                    return AdsApplyResponse.builder()
                            .applyId(apply.getId())
                            .advertisementId(ad.getId())
                            .packageName(ad.getAdPackage().getName())
                            .courseId(course.getId())
                            .courseThumbnail(course.getThumbnail())
                            .courseTitle(course.getTitle())
                            .authorId(author.getId())
                            .authorName(author.getFirstName()+" "+author.getLastName())
                            .authorEmail(author.getEmail())
                            .authorAvatar(author.getAvatar())
                            .status(apply.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());
    }
    public Map<Long, Double> getAverageRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.findAverageRatingsForCourses(courseIds);
        return results.stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Double) row[1]));
    }
//    public List<CourseResponse> getActiveCourseAdvertisements() {
//        List<AdsApply> activeAds = adsApplyRepository.findApprovedAndActiveAdvertisements();
//        List<Course> courses = activeAds.stream().map(AdsApply::getCourse).toList();
//        // Lấy danh sách ID của các khóa học
//        List<Long> courseIds = courses.stream().map(Course::getId).toList();
//
//        // Lấy avgRating cho từng khóa học
//        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
//        return courses.stream()
//                .map(course -> {
//                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
//                    courseResponse.setLevel(course.getLevel().name());
//                    courseResponse.setLabel(course.getLabel().name());
//                    courseResponse.setCountEnrolled(course.getCountEnrolled() != null ? course.getCountEnrolled() : 0);
//                    courseResponse.setStatus(course.getStatus().name());
//                    courseResponse.setCountRating(course.getCountRating() != null ? course.getCountRating() : 0);
//                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
//                    courseResponse.setAuthorName(course.getAuthor().getLastName() + " "
//                            + course.getAuthor().getFirstName());
//                    courseResponse.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
//                    return courseResponse;
//                })
//                .collect(Collectors.toList());
//    }
public CourseResponse getRandomActiveCourseAdvertisement() {
    List<AdsApply> activeAds = adsApplyRepository.findApprovedAndActiveAdvertisements();
    List<Course> courses = activeAds.stream().map(AdsApply::getCourse).toList();

    if (courses.isEmpty()) {
        return null; // hoặc throw exception tùy bạn xử lý
    }

    // Chọn ngẫu nhiên 1 khóa học
    Course randomCourse = courses.get(new Random().nextInt(courses.size()));

    // Tính avg rating
    Double avgRating = getAverageRatings(List.of(randomCourse.getId()))
            .getOrDefault(randomCourse.getId(), 0.0);

    CourseResponse courseResponse = courseMapper.toCourseResponse(randomCourse);
    courseResponse.setLevel(randomCourse.getLevel().name());
    courseResponse.setLabel(randomCourse.getLabel().name());
    courseResponse.setCountEnrolled(Optional.ofNullable(randomCourse.getCountEnrolled()).orElse(0));
    courseResponse.setStatus(randomCourse.getStatus().name());
    courseResponse.setCountRating(Optional.ofNullable(randomCourse.getCountRating()).orElse(0));
    courseResponse.setAvgRating(avgRating);
    courseResponse.setAuthorName(randomCourse.getAuthor().getLastName() + " " + randomCourse.getAuthor().getFirstName());
    courseResponse.setDiscount_price(voucherService.calculateDiscountedPrice(randomCourse.getPrice()));

    return courseResponse;
}






}

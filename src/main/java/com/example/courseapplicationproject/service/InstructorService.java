package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.AdminCreateDTO;
import com.example.courseapplicationproject.dto.request.AdminUpdateDTO;
import com.example.courseapplicationproject.dto.response.AdminDTO;
import com.example.courseapplicationproject.dto.response.InstructorListResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.InfoInstructorByCourseResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.Role;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.CourseReviewRepository;
import com.example.courseapplicationproject.repository.RoleRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import com.example.courseapplicationproject.service.interfaces.IInstructorService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class InstructorService implements IInstructorService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    CourseRepository courseRepository;
    CourseReviewRepository courseReviewRepository;
    PasswordEncoder passwordEncoder;
    CloudinaryService cloudinaryService;

    @Override
    public void becomeInstructor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.getIsTeacherApproved()) {
            Role role = roleRepository
                    .findByRoleName(Role.RoleType.INSTRUCTOR.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    @Override
    public InfoInstructorByCourseResponse getInfoInstructorByCourse(Long courseId) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        User user = course.getAuthor();
        String fullName = user.getFirstName() + " " + user.getLastName();
        return InfoInstructorByCourseResponse.builder()
                .id(user.getId())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .fullName(fullName)
                .reviewCount(courseReviewRepository.countReviewsByCourse(courseId))
                .avgRating(courseReviewRepository.avgRatingByCourse(courseId))
                .studentCount(userRepository.countStudentsByTeacherId(user.getId()))
                .courseCount(courseRepository.countCourseByByAuthor(user.getId()))
                .build();
    }


    public void addAdmin(AdminCreateDTO adminCreateDTO) {
        if (userRepository.existsByEmail(adminCreateDTO.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Role role = roleRepository
                .findByRoleName(Role.RoleType.ADMIN.toString())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = Set.of(role);
        User newUser = User.builder()
                .email(adminCreateDTO.getEmail())
                .isEnabled(true)
                .firstName(adminCreateDTO.getFirstName())
                .lastName(adminCreateDTO.getLastName())
                .dateOfBirth(adminCreateDTO.getBirthDate())
                .password(passwordEncoder.encode(adminCreateDTO.getPassword()))
                .roles(roles)
                .gender(adminCreateDTO.getGender())
                .avatar(adminCreateDTO.getAvatar())
                .build();
        userRepository.save(newUser);
    }
    public AdminUpdateDTO getAdmin(Long adminId){
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return AdminUpdateDTO.builder()
                .id(user.getId())
                .birthDate(user.getDateOfBirth())
                .gender(user.getGender())
                .firstName(user.getFirstName())
                .avatar(user.getAvatar())
                .lastName(user.getLastName())
                .isEnable(user.getIsEnabled())
                .build();
    }
    public List<InstructorListResponse> getAllInstructor(String keyword) {
        List<User> instructors = userRepository.findInstructors(keyword);

        // Lấy danh sách tổng số khóa học
        List<Object[]> courseCountsList = courseRepository.countCoursesByAuthor();
        Map<Long, Integer> courseCounts = courseCountsList.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));

        // Lấy danh sách tổng số học viên
        List<Object[]> studentCountsList = courseRepository.countStudentsByTeacher();
        Map<Long, Integer> studentCounts = studentCountsList.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));

        return instructors.stream()
                .map(admin -> InstructorListResponse.builder()
                        .id(admin.getId())
                        .name(admin.getFirstName() + " " + admin.getLastName())
                        .email(admin.getEmail())
                        .avatar(admin.getAvatar())
                        .isEnabled(admin.getIsEnabled())
                        .birthDate(admin.getDateOfBirth())
                        .gender(admin.getGender())
                        .totalCourses(courseCounts.getOrDefault(admin.getId(), 0))  // Lấy số khóa học
                        .totalStudents(studentCounts.getOrDefault(admin.getId(), 0))  // Lấy số học viên
                        .build())
                .collect(Collectors.toList());
    }
    public void updateAdmin(Long adminId, AdminUpdateDTO adminUpdateDTO) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (adminUpdateDTO.getFirstName() != null) admin.setFirstName(adminUpdateDTO.getFirstName());
        if (adminUpdateDTO.getLastName() != null) admin.setLastName(adminUpdateDTO.getLastName());
        if (adminUpdateDTO.getBirthDate() != null) admin.setDateOfBirth(adminUpdateDTO.getBirthDate());
        if (adminUpdateDTO.getGender() != null) admin.setGender(adminUpdateDTO.getGender());

        if (adminUpdateDTO.getPassword() != null && !adminUpdateDTO.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(adminUpdateDTO.getPassword()));
        }

        userRepository.save(admin);
    }
    public String uploadAvatar(MultipartFile avatar) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Map uploadResult = cloudinaryService.uploadImage(avatar);
        String secureUrl = uploadResult.get("secure_url").toString();

        user.setAvatar(secureUrl);
        userRepository.save(user);

        return secureUrl;
    }
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setIsEnabled(false);
        userRepository.save(user);
    }
}

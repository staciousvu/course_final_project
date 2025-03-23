package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.DiscussionRequest;
import com.example.courseapplicationproject.dto.response.DiscussionResponse;
import com.example.courseapplicationproject.entity.Discussion;
import com.example.courseapplicationproject.entity.Lecture;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.DiscussionRepository;
import com.example.courseapplicationproject.repository.LectureRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class DiscussionService {
    DiscussionRepository discussionRepository;
    LectureRepository lectureRepository;
    UserRepository userRepository;
    /**
     * Lấy danh sách discussion cho một lecture cụ thể với phân trang, sắp xếp và tìm kiếm.
     *
     * @param lectureId ID của lecture.
     * @param sortBy    Kiểu sắp xếp (mặc định "most_recent").
     * @param keyword   Từ khóa tìm kiếm (nếu có).
     * @param page      Số trang hiện tại.
     * @param size      Số lượng phần tử trên mỗi trang.
     * @return Trang chứa danh sách discussion.
     */
    public Page<DiscussionResponse> getDiscussionsByLecture(Long lectureId, String sortBy, String keyword, int page, int size) {
        Pageable pageable = buildPageable(sortBy, page, size);
        Page<Discussion> discussions;

        if (keyword != null && !keyword.trim().isEmpty()) {
            discussions = discussionRepository.findByLectureIdAndContentContainingIgnoreCase(lectureId, keyword, pageable);
        } else {
            discussions = discussionRepository.findByLectureId(lectureId, pageable);
        }

        return discussions.map(this::mapToResponse);
    }

    /**
     * Lấy danh sách discussion trên toàn hệ thống với phân trang và tìm kiếm.
     *
     * @param sortBy  Kiểu sắp xếp (mặc định "most_recent").
     * @param keyword Từ khóa tìm kiếm (nếu có).
     * @param page    Số trang hiện tại.
     * @param size    Số lượng phần tử trên mỗi trang.
     * @return Trang chứa danh sách discussion.
     */
    public Page<DiscussionResponse> getAllDiscussions(String sortBy, String keyword, int page, int size) {
        Pageable pageable = buildPageable(sortBy, page, size);
        Page<Discussion> discussions;

        if (keyword != null && !keyword.trim().isEmpty()) {
            discussions = discussionRepository.findByContentContainingIgnoreCase(keyword, pageable);
        } else {
            discussions = discussionRepository.findAll(pageable);
        }

        return discussions.map(this::mapToResponse);
    }

    /**
     * Lấy danh sách discussion cho một course cụ thể (qua mối quan hệ từ Lecture sang Course)
     * với phân trang, sắp xếp và tìm kiếm.
     *
     * @param courseId ID của course.
     * @param sortBy   Kiểu sắp xếp (mặc định "most_recent").
     * @param keyword  Từ khóa tìm kiếm (nếu có).
     * @param page     Số trang hiện tại.
     * @param size     Số lượng phần tử trên mỗi trang.
     * @return Trang chứa danh sách discussion.
     */
    public Page<DiscussionResponse> getDiscussionsByCourse(Long courseId, String sortBy, String keyword, int page, int size) {
        Pageable pageable = buildPageable(sortBy, page, size);
        Page<Discussion> discussions;

        if (keyword != null && !keyword.trim().isEmpty()) {
            discussions = discussionRepository.findByCourseIdAndContentContainingIgnoreCase(courseId, keyword, pageable);
        } else {
            discussions = discussionRepository.findByCourseId(courseId, pageable);
        }

        return discussions.map(this::mapToResponse);
    }

    /**
     * Tạo mới một Discussion cho một lecture cụ thể.
     * @param discussionRequest Đối tượng Discussion cần tạo.
     * @return Discussion đã được tạo.
     */
    public DiscussionResponse createDiscussion(DiscussionRequest discussionRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        Lecture lecture = lectureRepository.findById(discussionRequest.getLectureId())
                .orElseThrow(() -> new AppException(ErrorCode.LECTURE_NOT_FOUND));
        Discussion discussion = new Discussion();
        discussion.setLecture(lecture);
        discussion.setContent(discussionRequest.getContent());
        discussion.setUser(user);

        Discussion savedDiscussion = discussionRepository.save(discussion);
        return mapToResponse(savedDiscussion);
    }
    /**
     * Chuyển đổi `Discussion` entity thành `DiscussionResponse`.
     */
    private DiscussionResponse mapToResponse(Discussion discussion) {
        return DiscussionResponse.builder()
                .id(discussion.getId())
                .lectureId(discussion.getLecture().getId())
                .content(discussion.getContent())
                .author(discussion.getUser().getFirstName()+" " +discussion.getUser().getLastName())
                .createdAt(discussion.getCreatedAt())
                .countReplies(discussion.getReplies() != null ? discussion.getReplies().size() : 0)
                .build();
    }

    /**
     * Xây dựng đối tượng Pageable dựa trên kiểu sắp xếp, số trang và kích thước trang.
     *
     * @param sortBy Kiểu sắp xếp (mặc định "most_recent").
     * @param page   Số trang hiện tại.
     * @param size   Số lượng phần tử trên mỗi trang.
     * @return Pageable tương ứng.
     */
    private Pageable buildPageable(String sortBy, int page, int size) {
        Sort sort;
        if ("most_recent".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return PageRequest.of(page, size, sort);
    }
}

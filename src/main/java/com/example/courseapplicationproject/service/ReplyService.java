package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.ReplyRequest;
import com.example.courseapplicationproject.dto.response.ReplyResponse;
import com.example.courseapplicationproject.entity.Discussion;
import com.example.courseapplicationproject.entity.Reply;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.DiscussionRepository;
import com.example.courseapplicationproject.repository.ReplyRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class ReplyService {
    ReplyRepository replyRepository;
    DiscussionRepository discussionRepository;
    UserRepository userRepository;
    /**
     * Lấy danh sách các Reply của một Discussion cụ thể, sắp xếp theo thời gian tạo tăng dần.
     *
     * @param discussionId ID của Discussion.
     * @return Danh sách Reply.
     */
    public List<ReplyResponse> getRepliesByDiscussion(Long discussionId) {
        List<Reply> replies = replyRepository.findByDiscussionIdOrderByCreatedAtAsc(discussionId);
        return replies.stream().map(reply -> {
            ReplyResponse replyResponse = new ReplyResponse();
            replyResponse.setId(reply.getId());
            replyResponse.setCreatedAt(reply.getCreatedAt());
            replyResponse.setContent(reply.getContent());
            replyResponse.setAuthor(reply.getUser().getFirstName()+" "+reply.getUser().getLastName());
            return replyResponse;
        }).toList();
    }

    /**
     * Tạo mới một Reply cho một Discussion cụ thể.
     *
     * @param discussionId ID của Discussion mà Reply sẽ thuộc về.
     * @param replyRequest Reply cần tạo.
     * @return replyResponse đã được tạo.
     * @throws AppException nếu không tìm thấy Discussion với ID được cung cấp.
     */
    public ReplyResponse createReply(Long discussionId, ReplyRequest replyRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Optional<Discussion> discussionOpt = discussionRepository.findById(discussionId);
        if (discussionOpt.isEmpty()) {
            throw new AppException(ErrorCode.DISCUSSION_NOT_FOUND);
        }
        Reply reply = new Reply();
        reply.setDiscussion(discussionOpt.get());
        reply.setContent(replyRequest.getContent());
        reply.setUser(user);
        replyRepository.save(reply);
        return ReplyResponse.builder()
                .id(reply.getId())
                .createdAt(reply.getCreatedAt())
                .content(reply.getContent())
                .author(reply.getUser().getFirstName()+" "+reply.getUser().getLastName())
                .build();
    }
}

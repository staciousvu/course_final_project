package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.response.ConversationResponse;
import com.example.courseapplicationproject.dto.response.SimpleUserResponse;
import com.example.courseapplicationproject.entity.Conversation;
import com.example.courseapplicationproject.entity.Message;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.ConversationRepository;
import com.example.courseapplicationproject.repository.MessageRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import com.example.courseapplicationproject.service.interfaces.IConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ConversationResponse getOrCreateConversation(String emailStudent, String emailInstructor) {
        User student = userRepository.findByEmail(emailStudent)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        User instructor = userRepository.findByEmail(emailInstructor)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Nếu đã có conversation giữa student và instructor, trả về
        Optional<Conversation> optional = conversationRepository.findByStudentAndInstructor(student, instructor);
        if (optional.isPresent()) {
            return mapToResponse(optional.get());
        }
        // Ngược lại, tạo mới
        Conversation conversation = Conversation.builder()
                .student(student)
                .instructor(instructor)
                .lastMessageTime(LocalDateTime.now())
                .build();
        return mapToResponse(conversationRepository.save(conversation));
    }

    public List<ConversationResponse> getConversationsForStudent(String emailStudent) {
        User student = userRepository.findByEmail(emailStudent)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return conversationRepository.findByStudentOrderByLastMessageTimeDesc(student)
                .stream().map(this::mapToResponse).toList();
    }

    public List<ConversationResponse> getConversationsForInstructor(String emailInstructor) {
        User instructor = userRepository.findByEmail(emailInstructor)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return conversationRepository.findByInstructorOrderByLastMessageTimeDesc(instructor)
                .stream().map(this::mapToResponse).toList();
    }
    public Optional<Conversation> getById(Long id) {
        return conversationRepository.findById(id);
    }
    private ConversationResponse mapToResponse(Conversation conversation) {
        Message lastMessage = messageRepository
                .findTopByConversationOrderByCreatedAtDesc(conversation)
                .orElse(null);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .student(mapToSimpleUser(conversation.getStudent()))
                .instructor(mapToSimpleUser(conversation.getInstructor()))
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageTime(conversation.getLastMessageTime())
                .build();
    }
    private SimpleUserResponse mapToSimpleUser(User user) {
        return SimpleUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName()+" "+user.getLastName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }
}


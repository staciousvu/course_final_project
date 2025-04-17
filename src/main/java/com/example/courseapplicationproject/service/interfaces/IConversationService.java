package com.example.courseapplicationproject.service.interfaces;

import com.example.courseapplicationproject.dto.response.ConversationResponse;
import com.example.courseapplicationproject.entity.Conversation;
import com.example.courseapplicationproject.entity.User;

import java.util.List;
import java.util.Optional;

public interface IConversationService {
    public ConversationResponse getOrCreateConversation(String emailStudent, String emailInstructor);
    public List<ConversationResponse> getConversationsForStudent(String emailStudent);
    public List<ConversationResponse> getConversationsForInstructor(String emailInstructor);
    public Optional<Conversation> getById(Long id);
}

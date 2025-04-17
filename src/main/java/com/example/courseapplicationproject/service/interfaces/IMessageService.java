package com.example.courseapplicationproject.service.interfaces;

import com.example.courseapplicationproject.entity.Conversation;
import com.example.courseapplicationproject.entity.Message;
import com.example.courseapplicationproject.entity.User;

import java.util.List;

public interface IMessageService {
    public Message sendMessage(Long conversationId, User sender, String content);
    public List<Message> getMessages(Long conversationId);
    public void markMessagesAsRead(Long conversationId, User currentUser);
    public long countUnreadMessages(Long conversationId, User currentUser);
    public void notifyUser(User receiver, Message message);
    public boolean hasAccessToConversation(User user, Conversation conversation);
}

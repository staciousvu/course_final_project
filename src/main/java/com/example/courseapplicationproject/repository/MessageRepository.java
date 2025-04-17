package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Conversation;
import com.example.courseapplicationproject.entity.Message;
import com.example.courseapplicationproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    List<Message> findByConversationAndIsReadFalseAndSenderNot(Conversation conversation, User sender);

    long countByConversationAndIsReadFalseAndSenderNot(Conversation conversation, User currentUser);

    Optional<Message> findTopByConversationOrderByCreatedAtDesc(Conversation conversation);
}


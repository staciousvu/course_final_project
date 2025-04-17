package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Conversation;
import com.example.courseapplicationproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByStudentAndInstructor(User student, User instructor);

    List<Conversation> findByStudent(User student);

    List<Conversation> findByInstructor(User instructor);

    List<Conversation> findByStudentOrderByLastMessageTimeDesc(User student);

    List<Conversation> findByInstructorOrderByLastMessageTimeDesc(User instructor);

}


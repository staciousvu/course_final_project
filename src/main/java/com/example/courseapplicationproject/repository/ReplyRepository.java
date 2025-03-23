package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply,Long> {
    List<Reply> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId);
}

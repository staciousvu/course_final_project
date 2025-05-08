package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    List<Quiz> findQuizByIsDeletedFalseAndCourseId(Long courseId);
}

package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer,Integer> {
}

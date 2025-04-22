package com.example.courseapplicationproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserSearchKeywordHistory;

@Repository
public interface SearchHistoryRepository extends JpaRepository<UserSearchKeywordHistory, Long> {
    @Query("SELECT u.keyword FROM UserSearchKeywordHistory u " +
            "WHERE u.user.id = :userId " +
            "GROUP BY u.keyword " +
            "ORDER BY MAX(u.createdAt) DESC")
    List<String> findByUserId(@Param("userId") Long userId);
}

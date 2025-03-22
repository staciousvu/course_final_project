package com.example.courseapplicationproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserSearchKeywordHistory;

@Repository
public interface SearchHistoryRepository extends JpaRepository<UserSearchKeywordHistory, Long> {
    @Query(
            "select distinct(u.keyword) from UserSearchKeywordHistory u where u.user.id =:userId order by u.createdAt desc")
    List<String> findByUserId(@Param("userId") Long userId);
}

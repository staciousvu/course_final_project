package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Section;
import com.example.courseapplicationproject.entity.UserSearchKeywordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<UserSearchKeywordHistory, Long> {
    @Query("select distinct(u.keyword) from UserSearchKeywordHistory u where u.user.id =:userId order by u.createdAt desc")
    List<String> findByUserId(@Param("userId") Long userId);
}

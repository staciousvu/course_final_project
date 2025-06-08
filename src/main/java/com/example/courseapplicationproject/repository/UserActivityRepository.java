package com.example.courseapplicationproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserActivity;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
//    @Query("select distinct(u) from UserActivity u where u.user.id =:userId order by u.createdAt desc")
//    List<UserActivity> findIdsActivityByUserId(Long userId);

    @Query("""
    SELECT DISTINCT u FROM UserActivity u
    JOIN FETCH u.course c
    JOIN FETCH c.category
    WHERE u.user.id = :userId
    ORDER BY u.createdAt DESC
""")
    List<UserActivity> findIdsActivityByUserId(@Param("userId") Long userId);


}

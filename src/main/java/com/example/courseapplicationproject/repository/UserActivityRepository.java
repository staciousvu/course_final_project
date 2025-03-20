package com.example.courseapplicationproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserActivity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    @Query("select u.course.id from UserActivity u where u.user.id =:userId")
    List<Long> findIdsActivityByUserId(Long userId);
}

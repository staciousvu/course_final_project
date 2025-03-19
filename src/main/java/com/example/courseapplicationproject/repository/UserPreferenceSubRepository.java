package com.example.courseapplicationproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserPreferenceSub;

@Repository
public interface UserPreferenceSubRepository extends JpaRepository<UserPreferenceSub, Long> {
    void deleteAllByUserId(Long userId);

    List<UserPreferenceSub> findAllByUserId(Long userId);
}

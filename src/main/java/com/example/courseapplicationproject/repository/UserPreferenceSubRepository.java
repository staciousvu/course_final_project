package com.example.courseapplicationproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserPreferenceSub;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserPreferenceSubRepository extends JpaRepository<UserPreferenceSub, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserPreferenceSub ups WHERE ups.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    List<UserPreferenceSub> findAllByUserId(Long userId);
}

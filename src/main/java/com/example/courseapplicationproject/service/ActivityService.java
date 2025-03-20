package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.entity.UserActivity;
import com.example.courseapplicationproject.repository.UserActivityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    UserActivityRepository userActivityRepository;
    public void saveActivity(User user, Course course){
        userActivityRepository.save(new UserActivity(user, course));
    }
}

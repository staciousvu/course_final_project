package com.example.courseapplicationproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
@EnableElasticsearchRepositories(basePackages = "com.example.courseapplicationproject.elasticsearch.repository")
public class CourseApplicationProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplicationProjectApplication.class, args);
    }
}

package com.example.courseapplicationproject.initiliazer;

import com.example.courseapplicationproject.elasticsearch.document.CourseDocument;
import com.example.courseapplicationproject.elasticsearch.repository.CourseElasticRepository;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncDataCourseElastic {
    private final CourseRepository courseRepository;
    private final CourseElasticRepository courseElasticRepository;
    @PostConstruct
    public void init() {
        log.info("Starting sync data course to Elasticsearch...");
        List<Course> courses = courseRepository.findAll();

        List<CourseDocument> courseDocuments = courses.stream().map(course ->
                CourseDocument.builder()
                        .id(course.getId().toString())
                        .title(course.getTitle())
                        .subtitle(course.getSubtitle())
                        .description(course.getDescription())
                        .build()
        ).toList();
        courseElasticRepository.saveAll(courseDocuments);
        log.info("Sync completed!");
    }
    @EventListener(ContextClosedEvent.class)
    public void cleanup() {
        try {
            log.info("Deleting all courses from Elasticsearch before shutdown...");
            courseElasticRepository.deleteAll();
            log.info("Delete courses elastic completed!");
        } catch (Exception e) {
            log.warn("Failed to delete courses from Elasticsearch: {}", e.getMessage());
        }
    }
}

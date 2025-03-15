package com.example.courseapplicationproject.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.elasticsearch.document.CourseDocument;

@Repository
public interface CourseElasticRepository extends ElasticsearchRepository<CourseDocument, String> {
    List<CourseDocument> findByTitleContainingOrDescriptionContainingOrSubtitleContaining(String keyword);
}

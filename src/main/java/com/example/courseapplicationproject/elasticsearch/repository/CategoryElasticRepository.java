//package com.example.courseapplicationproject.elasticsearch.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import com.example.courseapplicationproject.elasticsearch.document.CategoryDocument;
//
//@Repository
//public interface CategoryElasticRepository extends ElasticsearchRepository<CategoryDocument, String> {
//    Optional<CategoryDocument> findBySlug(String slug);
//
//    List<CategoryDocument> findByIsActiveTrue();
//}

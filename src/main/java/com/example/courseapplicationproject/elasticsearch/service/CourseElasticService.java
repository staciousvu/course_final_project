package com.example.courseapplicationproject.elasticsearch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.elasticsearch.document.CourseDocument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CourseElasticService {
    private final ElasticsearchOperations elasticsearchOperations;

    public List<String> fuzzySearch(String keyword) {
        Criteria criteria = new Criteria("title")
                .fuzzy(keyword)
                .or(new Criteria("description").fuzzy(keyword))
                .or(new Criteria("subtitle").fuzzy(keyword));

        CriteriaQuery query = new CriteriaQuery(criteria);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toList());
    }
}

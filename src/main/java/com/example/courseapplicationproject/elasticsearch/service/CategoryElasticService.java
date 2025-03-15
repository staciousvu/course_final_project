package com.example.courseapplicationproject.elasticsearch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.elasticsearch.document.CategoryDocument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CategoryElasticService {
    ElasticsearchOperations elasticsearchOperations;

    public List<CategoryDocument> searchCategory(String keyword) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b.should(
                                s -> s.multiMatch(m -> m.query(keyword).fields("name", "description")))
                        .should(s -> s.match(m -> m.field("name").query(keyword).fuzziness("AUTO")))))
                .build();
        SearchHits<CategoryDocument> searchHits = elasticsearchOperations.search(nativeQuery, CategoryDocument.class);
        return searchHits.hasSearchHits()
                ? searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList())
                : List.of();
    }
}

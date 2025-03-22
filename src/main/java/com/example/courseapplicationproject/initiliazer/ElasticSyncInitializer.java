// package com.example.courseapplicationproject.initiliazer;
//
// import java.util.List;
//
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.courseapplicationproject.elasticsearch.document.CategoryDocument;
// import com.example.courseapplicationproject.elasticsearch.repository.CategoryElasticRepository;
// import com.example.courseapplicationproject.entity.Category;
// import com.example.courseapplicationproject.mapper.CategoryMapper;
// import com.example.courseapplicationproject.repository.CategoryRepository;
//
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @RequiredArgsConstructor
// @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
// @Component
// public class ElasticSyncInitializer {
//    CategoryMapper categoryMapper;
//    CategoryRepository categoryRepository;
//    CategoryElasticRepository categoryElasticRepository;
//
//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional
//    public void syncDataToElasticSearch() {
//        log.info("Start syncing data to elastic search");
//        List<Category> categories = categoryRepository.findAll();
//        List<CategoryDocument> categoryDocuments =
//                categories.stream().map(categoryMapper::toCategoryDocument).toList();
//        categoryElasticRepository.saveAll(categoryDocuments);
//        log.info("End syncing data to elastic search ,total categories : {}", categories.size());
//    }
// }

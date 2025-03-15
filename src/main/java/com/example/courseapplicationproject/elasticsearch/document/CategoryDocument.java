package com.example.courseapplicationproject.elasticsearch.document;

import java.util.Set;

import jakarta.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.example.courseapplicationproject.dto.response.CategoryBasicResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "categories")
public class CategoryDocument {
    @Id
    String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    String description;

    @Field(type = FieldType.Keyword)
    String slug;

    @Field(type = FieldType.Boolean)
    Boolean isActive;

    @Field(type = FieldType.Integer)
    Integer displayOrder;

    @Field(type = FieldType.Long)
    Long parentCategoryId;

    @Field(type = FieldType.Nested)
    Set<CategoryBasicResponse> subCategories;
}

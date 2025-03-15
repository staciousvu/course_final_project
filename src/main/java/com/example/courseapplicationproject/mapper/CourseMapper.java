package com.example.courseapplicationproject.mapper;

import org.mapstruct.*;

import com.example.courseapplicationproject.dto.request.CourseCreateRequest;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(source = "level", target = "level", qualifiedByName = "mapLevel")
    Course toCourse(CourseCreateRequest request);

    @Named("mapLevel")
    static Course.LevelCourse mapLevel(String level) {
        return Course.LevelCourse.valueOf(level.toUpperCase());
    }

    @Mapping(source = "category.name", target = "categoryName")
    CourseResponse toCourseResponse(Course course);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCourse(@MappingTarget Course course, CourseCreateRequest request);
}

package com.example.courseapplicationproject.mapper;

import org.mapstruct.*;

import com.example.courseapplicationproject.dto.request.SectionCreateRequest;
import com.example.courseapplicationproject.entity.Section;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSection(@MappingTarget Section section, SectionCreateRequest sectionCreateRequest);
}

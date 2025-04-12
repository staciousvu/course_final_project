package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private Boolean isActive;
    private Integer displayOrder;
}

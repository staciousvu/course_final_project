package com.example.courseapplicationproject.specifications;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.example.courseapplicationproject.entity.Course;

public class CourseSpecification {
    public static Specification<Course> hasLanguage(String language) {
        return (language == null || language.isEmpty())
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("language"), language);
    }
    public static Specification<Course> isActiveStatus(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) return null;

            return isActive
                    ? criteriaBuilder.equal(root.get("isActive"), Course.IsActive.ACTIVE)
                    : criteriaBuilder.equal(root.get("isActive"), Course.IsActive.INACTIVE);
        };
    }

    public static Specification<Course> hasAccepted(Boolean isAccepted) {
        return (root, query, criteriaBuilder) -> {
            if (isAccepted == null) return null;
            return isAccepted
                    ? criteriaBuilder.equal(root.get("status"), Course.CourseStatus.ACCEPTED)
                    : criteriaBuilder.notEqual(root.get("status"), Course.CourseStatus.ACCEPTED);
        };
    }

    public static Specification<Course> hasLevel(String level) {
        return (root, query, criteriaBuilder) ->
                (level == null || level.isEmpty()) ? null : criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<Course> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> (categoryId == null)
                ? null
                : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Course> isFree(Boolean isFree) {
        return (root, query, criteriaBuilder) -> {
            if (isFree == null) {
                return null;
            }
            return isFree
                    ? criteriaBuilder.equal(root.get("price"), BigDecimal.ZERO)
                    : criteriaBuilder.greaterThan(root.get("price"), BigDecimal.ZERO);
        };
    }

    public static Specification<Course> hasDuration(Integer minDuration, Integer maxDuration) {
        return (root, query, criteriaBuilder) -> {
            if (minDuration == null && maxDuration == null) return null;
            if (minDuration == null) return criteriaBuilder.lessThan(root.get("duration"), maxDuration);
            if (maxDuration == null) return criteriaBuilder.greaterThan(root.get("duration"), minDuration);
            return criteriaBuilder.between(root.get("duration"), minDuration, maxDuration);
        };
    }
}

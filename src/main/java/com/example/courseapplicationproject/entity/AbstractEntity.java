package com.example.courseapplicationproject.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity<T extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    T id;

    @Column(name = "created_by")
    @CreatedBy
    String createdBy;

    @Column(name = "updated_by")
    @LastModifiedBy
    String updatedBy;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
// <T extends Serializable>:
// Đây là cách khai báo một tham số kiểu (generic type parameter) có tên là T.
// Phần extends Serializable có nghĩa là T phải là kiểu Serializable
// hoặc một kiểu kế thừa từ Serializable. Điều này có nghĩa là bất kỳ kiểu nào
// được truyền vào tham số T đều phải có khả năng tuần tự hóa (serialize).
//
// implements Serializable:
// Lớp AbstractEntity implements interface Serializable,
// nghĩa là lớp này có thể được tuần tự hóa. Điều này có nghĩa là đối tượng
// của lớp AbstractEntity (và các lớp con kế thừa từ nó) có thể được chuyển đổi
// thành chuỗi byte để lưu trữ hoặc truyền qua mạng.

// @MappedSuperclass trong Spring (đặc biệt là trong JPA/Hibernate)
// được sử dụng để định nghĩa một lớp cha chứa các trường (fields)
// và mapping (liên kết) chung cho các lớp entity khác

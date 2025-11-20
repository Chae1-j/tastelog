package com.tastelog.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

// 애플리케이션의 사용자 계정 정보를 DB users 테이블과 매핑하는 JPA 엔티티
// This is the entity which mapped between user account information and DB users table
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Length(max=100)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Length(max=255)
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    @Length(max=50)
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String role;

    @Length(max=500)
    @Column(length = 500)
    private String bio;

    protected User() {} // 기본 생성자
}

package com.tastelog.backup.user.dto;

import java.time.OffsetDateTime;

public class UserProfileResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String phone;
    private String bio;
    private String avatarUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserProfileResponse() {  // 기본 생성자 >> 스프링에서 JSON으로 자동 변환할 때 필수.
    }

    public UserProfileResponse(Long userId, String email, String nickname,
                               String phone, String bio, String avatarUrl,
                               OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
/*
 로그인한 사용자의 프로필 조회 시, 서버가 클라이언트로 보내는 응답 데이터 형식을 정의한 클래스
 * DTO란? 데이터 전달 전용 객체
 */
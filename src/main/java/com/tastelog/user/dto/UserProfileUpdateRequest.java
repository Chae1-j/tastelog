package com.tastelog.user.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class UserProfileUpdateRequest {

    @Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
    private String nickname;

    @Pattern(regexp = "^[0-9\\-]{10,13}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

    @Size(max = 200, message = "소개글은 최대 200자까지 입력할 수 있습니다.")
    private String bio;

    @Size(max = 255, message = "프로필 이미지 URL은 최대 255자까지 입력할 수 있습니다.")
    private String avatarUrl;

    public UserProfileUpdateRequest() {
    }

    public UserProfileUpdateRequest(String nickname, String phone, String bio, String avatarUrl) {
        this.nickname = nickname;
        this.phone = phone;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
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
}
/*
회원 프로필 수정 시 사용자가 보낸 데이터를 담는 구조체(요청용 DTO)
 */
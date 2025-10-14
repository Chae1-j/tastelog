package com.tastelog.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterResponse {
    private Long id;
    private String email;
    private String nickname;
}

/*
응답 DTO역할
서버가 클라이언트에게 돌려주는 데이터 형식을 정의한 응답전용 DTO
Controller에서 엔티티를 반환하지 않고 필요한 일부정보만 따로 담아 응답.
 */
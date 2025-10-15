package com.tastelog.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;
}

/*
- 클라이언트가 로그인할 때 보낸 JSON 바디를 안전하게 수신하는 "입력 전용 객체"
- 컨트롤러 들어오기 직전에 형식 검증

로그인 흐름도
AuthController → LoginRequest → AuthController → AuthService → UserRepository → AuthService → JwtTokenProvider → AuthService → LoginResponse → AuthController

 */
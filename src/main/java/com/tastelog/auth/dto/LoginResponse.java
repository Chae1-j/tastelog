package com.tastelog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // 직렬화 시 값 읽기용 접근자 자동 생성
@AllArgsConstructor // 두 필드를 받는 생성자 자동 생성(불변 패턴에 유리)
public class LoginResponse {
    private String accessToken;
    private String tokenType; // "Bearer"
}

/*
클라이언트로 전달할 응답 전용DTO. 따라서 Setter 불필요.
- 직렬화 : 객체를 바이트 데이터/문자열(JSON, XML 등) 형태로 바꾸는 과정.
- 불변(Immutable) 패턴: 객체가 한 번 만들어진 뒤 상태(필드 값)를 바꿀 수 없게 만드는 설계 방식.
    > Response가 불변 패턴인 이유? : 필드가 private final일 때/
- 토큰 생성 이유? 클라이언트가 로그인 성공 후에도 계속 인증을 유지하기 위해서.
    > 토큰 : 사용자(클라이언트)가 보유 하고 있음.
      세션 : 서버에서 쿠키에 저장함.
 */
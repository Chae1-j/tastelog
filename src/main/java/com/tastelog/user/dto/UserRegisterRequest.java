package com.tastelog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
/*
lombok >
 */

@Getter
@Setter
public class UserRegisterRequest {
    @NotBlank(message = "이메일은 필수입니다.") // null, 공백만 있는 문자열 모두 거부.
    @Email(message = "이메일 형식이 올바르지 않습니다.") // 표준 이메일 형식을 검증
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.") // 문자열 길이 범위를 제한
    private String nickname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    private String password;
}

/*
회원가입 요청 바디를 안전하게 받는 입력전용 DTO. 엔티티 직접 바인딩 시 DB 스키마 변경이 api에 그대로 전파. api스펙을 안정적으로 분리하고, 입력검증을 처리함

 */
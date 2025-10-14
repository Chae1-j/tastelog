package com.tastelog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

lombok > 컨트롤 바인딩 및 서비스 계층 전달 시 필드 접근 단순화
    반복적인 코드 자동 생성.(컴파일 시점에 해당 메서드들 자동 생성)
    getter, setter, NoArgsConstructor(파라미터 없는 기본 생성자-JPA에서 엔티티 생성 시 필요) 등등의 어노테이션이 있음

Jakarta Bean Validation 입력값 검증을 자동으로 수행하기 위한 표준 스펙.
     Controller Method에서 @RequestBody, @Valid dto 선언 > 요청이 들어오면 스프링이 DTO 필드에 지정된 검증 어노테이션을 검사 > 위반 시 controller 진입전에 예외발생함


DTO: 입력을 안전하게 받고 검증
Service: 규칙(중복, 해시, 트랜잭션) 적용
Repository: DB 저장/조회
Response DTO: 필요한 정보만 반환
Security(개발용): permitAll + CSRF off로 테스트 편의성 확보

 */
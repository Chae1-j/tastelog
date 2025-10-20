package com.tastelog.auth;

import com.tastelog.auth.dto.LoginRequest;
import com.tastelog.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        // ResponseEntity<T>는 HTTP 상태코드, 헤더, 바디를 모두 제어할 수 있는 컨테이너.
        // @RequestBody 요청 바디(JSON)을 LoginRequest로 역직렬화. 키 이름 - DTO 필드명
        // @Valid LoginRequest에 선언된 Bean Validation을 메서드 진입 전에 검사.
        // LoginRequest req 역직렬화, 검증을 통과한 안전한 입력 객체

        LoginResponse res = authService.login(req);
        return ResponseEntity.ok(res); // LoginResponse가 JSON으로 직렬화되어 응답됨.
    }
}


/*

로그인 api 엔드포인트를 제공하는 프레젠테이션 계층
HTTP 요청(JSON)을 받아 서비스 계층(AuthService)에 위임, HTTP 응답(JSON)을 반환

 */
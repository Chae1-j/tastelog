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
        LoginResponse res = authService.login(req);
        return ResponseEntity.ok(res);
    }
}


/*

로그인 api 엔드포인트를 제공하는 프레젠테이션 계층
HTTP 요청(JSON)을 받아 서비스 계층(AuthService)에 위임, HTTP 응답(JSON)을 반환

 */
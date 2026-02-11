package com.tastelog.backup.user;

import com.tastelog.backup.common.response.ApiResponse;
import com.tastelog.backup.user.dto.UserProfileResponse;
import com.tastelog.backup.user.dto.UserProfileUpdateRequest;
import com.tastelog.backup.user.dto.UserRegisterRequest;
import com.tastelog.backup.user.dto.UserRegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController // REST API 요청을 처리하는 컨트롤러임을 표시. 메서드 리턴값 JSON형태로 자동 변환
@RequiredArgsConstructor // final이 붙은 필드 자동으로 생성자 주입. Lombok 어노테이션
@RequestMapping("/api/users") // 모든 엔드포인터 URL앞에 /api/users가 붙음
public class UserController {

    private final UserService userService;

//    @PostMapping("/register")
//    public ResponseEntity<User> register(@RequestBody User user) {
//        User saved = userService.registerUser(user);
//        return ResponseEntity.ok(saved); // User객체를 JSON으로 응답. 200 OK를 함께 반환
//    }

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        try {
//            User saved = userService.registerUser(user);
//            return ResponseEntity.ok(saved);
//        } catch (Exception e) {
//            e.printStackTrace(); // 콘솔에 정확한 예외 확인
//            return ResponseEntity.internalServerError().body(e.getClass().getSimpleName() + ": " + e.getMessage());
//        }
//    }


//    @PostMapping("/register")
//    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest req) {
//        UserRegisterResponse res = userService.register(req);
//        return ResponseEntity.ok(res);
//    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse result = userService.register(request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        Long userId = currentUserId();  // 보안 컨텍스트에서 현재 로그인한 사용자 ID를 추출. JWT 인증 필터가 미리 넣어둔 값을 읽는 구조
        UserProfileResponse result = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        Long userId = currentUserId();
        UserProfileResponse result = userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /*
	  SecurityContext에 저장된 인증 정보에서 현재 로그인 사용자의 식별자(userId)를 추출.
	  - JwtAuthenticationFilter에서 Authentication의 name(=getName())을 userId 문자열로 설정해두었다는 전제.
	  - 인증 정보가 없거나 형식이 잘못된 경우 예외를 던져 전역 예외 처리기로 위임.
     */
    private Long currentUserId() {  // 현재 로그인한 사용자의 ID를 가져오는 기능 발급된 JWT 토큰이 Security Context에 저장
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication(); // 스프링 시큐리티의 보안 컨텍스트에서 현재 요청의 인증 객체를 꺼냄
        if(auth == null || auth.getName() == null) {
            throw new AuthenticationCredentialsNotFoundException("인증이 필요합니다."); // 로그인 정보가 없을 때 예외를 던짐. 즉 토근이 없거나 만료된 경우.
        }
        try {
            return Long.parseLong(auth.getName());
        } catch (NumberFormatException e) { // 포맷오류
            throw new IllegalStateException("잘못된 인증 정보 형식입니다."); // 
        }
    }

}

/*
1.
@Controller
전통적인 MVC 컨트롤러.
**뷰(html, jsp, thymeleaf 등)**를 반환하는 데 주로 사용.
메서드 리턴값이 String이면 → 뷰 이름으로 해석되어 템플릿을 찾음.

@RestController
@Controller + @ResponseBody 합친 것.
메서드 리턴값이 그대로 HTTP 응답 본문으로 전송됨 (주로 JSON).
API 서버(백엔드) 개발 시 기본적으로 사용.

2. final은 반드시 값이 있어야 하고, 중간에 변경 불가.
UserService가 UserRepository 없이 동작할 수 없으니, 객체가 생성될 때 무조건 주입되도록 강제.
@RequiredArgsConstructor가 생성자를 자동으로 만들어 주입해 줌
> 필수 의존성을 안정적으로 보장

3. ResponseEntity란?
스프링에서 제공하는 응답 래퍼(wrapper) 객체. 단순히 JSON 본문만 반환하는 게 아니라, HTTP 상태 코드, 헤더, 바디까지 설정가능


*/
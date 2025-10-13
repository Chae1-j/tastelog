package com.tastelog.user;

import com.tastelog.user.dto.UserRegisterRequest;
import com.tastelog.user.dto.UserRegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest req) {
        UserRegisterResponse res = userService.register(req);
        return ResponseEntity.ok(res);
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
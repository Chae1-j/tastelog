package com.tastelog.auth;

import com.tastelog.auth.dto.LoginRequest;
import com.tastelog.auth.dto.LoginResponse;
import com.tastelog.config.JwtTokenProvider;
import com.tastelog.user.User;
import com.tastelog.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service    // 스프링 서비스 빈으로 등록.
@RequiredArgsConstructor // final 필드들을 인자로 받는 생성자 자동 생성
public class AuthService {

    private final UserRepository userRepository;    // 사용자 조회를 위한 JPA 리포지토리 의존성
    private final PasswordEncoder passwordEncoder;  // 비밀번호 검증용 암호화기
    private final JwtTokenProvider jwtTokenProvider;// JWT 생성기 액세스 토큰/ 리프레시 토큰 생성 기능 제공

    public LoginResponse login(LoginRequest req) {  // 로그인 진입 메서드
        User user = userRepository.findByEmail(req.getEmail()) //
                .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) { // 저장된 해시 user.getPassword()를 passwordEncoder.matches(...)로 비교
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), List.of("ROLE_USER")); // JWT 토큰 생성 단계
        // createToken(사용자 식별값, 권한 리스트)
        return new LoginResponse(token, "Bearer"); // 토큰, 토큰 타입 문자열을 담아 응답 DTO 반환 > 컨트롤러에서 json으로 직렬화됨.
    }
}
/*
** 컨트롤러에서 위임받은 비즈니스 로직을 수행 **
 */

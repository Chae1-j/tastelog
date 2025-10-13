package com.tastelog.user;

import com.tastelog.user.dto.UserRegisterRequest;
import com.tastelog.user.dto.UserRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 스프링의 서비스 계층 컴포넌트임을 표시. 컨트롤러와 레포지토리 사이에서 비즈니스 로직(규칙/처리)를 담당
@RequiredArgsConstructor // Lombok 어노테이션. final이 붙은 필드들에 자동으로 생성자 주입. 스프링이 의존성을 넣어줌
public class UserService {

    private final UserRepository userRepository; // UresRepository를 주입받음. 엔티티를 DB에 저장하거나 조회할 때 활용.
    private final PasswordEncoder passwordEncoder; //스프링 시큐리티에서 제공하는 비밀번호 암호화 도구를 주입.

//    public User registerUser(User user) {
//        // 비밀번호 암호화
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
    @Transactional
    public UserRegisterResponse register(UserRegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(req.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setNickname(req.getNickname());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        User saved = userRepository.save(user);
        return new UserRegisterResponse(saved.getId(), saved.getEmail(), saved.getNickname());
    }

}

/*
1. 서비스 계층(Service Layer)이란 무엇인가?
    - 계층형 아키텍쳐
        Controller: 사용자의 요청을 받고 응답을 돌려주는 역할 (API 진입점)
        Service: 핵심 비즈니스 로직(규칙, 처리)을 담당하는 중간 계층
        Repository: DB와 직접 통신하는 계층
컨트롤러는 단순히 요청을 전달하고 응답을 반환, 규칙은 서비스 계층에서 관리.
2. final : 한번 초기화되면 더 이상 바꿀 수 없음(불변성)
   의존성(Dependency): 이 클래스가 동작하기 위해 반드시 필요한 외부 객체
    > final은 변경 불가 보장 / 의존성은 외부 필요 객체 / 생성자 주입은 반드시 값을 채우도록 강제
3. 주입 : 필요한 객체를 외부에서 넣어주는 것(spring이 넣어줌)
 */

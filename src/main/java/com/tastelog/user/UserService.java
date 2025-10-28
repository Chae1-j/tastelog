package com.tastelog.user;

import com.tastelog.user.dto.UserRegisterRequest;
import com.tastelog.user.dto.UserRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

import com.tastelog.user.dto.UserProfileResponse;
import com.tastelog.user.dto.UserProfileUpdateRequest;

@Service // 스프링의 서비스 계층 컴포넌트임을 표시. 컨트롤러와 레포지토리 사이에서 비즈니스 로직(규칙/처리)를 담당
@RequiredArgsConstructor // Lombok 어노테이션. final이 붙은 필드들에 자동으로 생성자 주입. 스프링이 의존성을 넣어줌
public class UserService {
    // 의존성 주입 > 두 객체 new로 생성 안함. 스프링이 bean으로 관리하고 있으므로 @Required...가 자동생성 생성자를 주입
    private final UserRepository userRepository; // UresRepository를 주입받음. 엔티티를 DB에 저장하거나 조회할 때 활용.
    private final PasswordEncoder passwordEncoder; //스프링 시큐리티에서 제공하는 비밀번호 암호화 도구를 주입.

//    public User registerUser(User user) {
//        // 비밀번호 암호화
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
    @Transactional // 메서드 내 작업(insert, update, delete 등)을 하나의 트랜잭션으로 묶어줌. 실패 시 모든 작업 롤백함.
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

    /**
     * 현재 로그인한 사용자의 프로필을 조회.
     * @param userId 토큰에서 추출한 본인 식별자
     * @return UserProfileResponse (민감정보 제외한 조회용 DTO)
     */
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return toUserProfileResponse(user);
    }

    /**
     * 현재 로그인한 사용자의 프로필을 수정합니다.
     * - null인 필드는 '미변경'
     * - 빈 문자열은 '비우기(덮어쓰기)'로 처리합니다.
     * @param userId 토큰에서 추출한 본인 식별자
     * @param request 수정 요청 DTO
     * @return 수정 후 최신 프로필 DTO
     */
    public UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // null → 변경하지 않음, null이 아니면 그대로 덮어씀(빈문자 포함)
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // 수정 시각 갱신 (엔티티에 필드가 있다면)
        try {
            user.setUpdatedAt(OffsetDateTime.now());
        } catch (Exception ignore) {
            // 엔티티에 updatedAt 세터가 없거나 JPA Auditing 사용 시 무시
        }

        User saved = userRepository.save(user);
        return toUserProfileResponse(saved);
    }

    /**
     * User 엔티티 → UserProfileResponse 매핑 도우미
     * 엔티티의 민감정보(비밀번호/권한 등)는 DTO로 노출하지 않습니다.
     */
    private UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


}

/*
1. 서비스 계층(Service Layer)이란 무엇인가?
    - 계층형 아키텍쳐
        Controller: 사용자의 요청을 받고 응답을 돌려주는 역할 (API 진입점)
        Service: 핵심 비즈니스 로직(규칙, 처리)을 담당하는 중간 계층
        Repository: DB와 직접 통신하는 계층
컨트롤러는 단순히 요청을 전달하고 응답을 반환, 규칙은 서비스 계층에서 관리. 데이터의 유효성과 규칙 검증, 데이터베이스 저장 연결
2. final : 한번 초기화되면 더 이상 바꿀 수 없음(불변성)
   의존성(Dependency): 이 클래스가 동작하기 위해 반드시 필요한 외부 객체
    > final은 변경 불가 보장 / 의존성은 외부 필요 객체 / 생성자 주입은 반드시 값을 채우도록 강제
3. 주입 : 필요한 객체를 외부에서 넣어주는 것(spring이 넣어줌)
 */

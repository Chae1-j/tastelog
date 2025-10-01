package com.tastelog.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // 제네릭 첫 번째 : 엔티티, 제네릭 두 번째 : 기본 키 타입
    
    // 이메일로 회원찾기
    Optional<User> findByEmail(String email);

    // 특정 이메일이 존재하는지 여부 확인
    boolean existsByEmail(String email);
}

/*

-- User Entity 와 DB 연결 다리 역할
1. 제너릭 타입 : 클래스나 인터페이스가 타입을 외부에서 받아서 재사용 가능하도록 만든 문법.
2. JpaRepository : JPA가 제공하는 레포지토리 인터페이스. 상속 시 CRUD 자동 생성됨.
3. Optional<T> : 값이 있을수도있고 없을수도 있음을 의미

 */
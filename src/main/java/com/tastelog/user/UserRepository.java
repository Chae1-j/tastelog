package com.tastelog.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// @Repository(:데이터 접근계층을 의미) 없어도 됨. 현재는 JpaRepository를 상송하겸ㄴ 자동으로 감지해서 프록시 객체로 등록함.
public interface UserRepository extends JpaRepository<User, Long> { // 제네릭 첫 번째 : 엔티티, 제네릭 두 번째 : 기본 키 타입
    
    // 이메일로 회원찾기
    Optional<User> findByEmail(String email); // 이메일로 회원 한 명을 찾기. 반환타입 Optional? > 검색결과가 있을 수도 있고 없을 수도 있기 때문

    // 특정 이메일이 존재하는지 여부 확인
    boolean existsByEmail(String email);

    // 닉네임이 중복되는지 확인
    boolean existsByNickname(String nickname);
}

/*

-- User Entity 와 DB 연결 다리 역할(DAO 역할)
DB와 직접 통신
JPA를 통해 CRUD 수행


1. 제너릭 타입 : 클래스나 인터페이스가 타입을 외부에서 받아서 재사용 가능하도록 만든 문법.
2. JpaRepository : JPA가 제공하는 레포지토리 인터페이스. 상속 시 CRUD 자동 생성됨. JpaRepository<T, ID> 에서 T > 엔티티 타입, ID > 기본기 타입
3. Optional<T> : 값이 있을수도있고 없을수도 있음을 의미

 */
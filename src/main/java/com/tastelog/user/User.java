package com.tastelog.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 이 클래스가 JPA에서 DB테이블과 매핑되는 엔티티임을 표시
@Getter // Lombok 어노테이션 > 필드마다 getter/setter 자동생성
@Setter // 이하 동일
@NoArgsConstructor // 파라미터 없는 생성자 자동 생성(JPA가 내부적으로 객체를 생성할 때 필요)
@Table(name = "users") // DB테이블 이름을 users로 지정.
public class User {

    @Id // 기본 키(PK) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 AUTO_INCREMENT 방식으로 키를 생성
    private Long id;

    @Email // 유효성 검사(이 값이 이메일 형식인지 확인)
    @NotBlank // null, 빈문자열, 공백 불가
    @Column(nullable = false, unique = true) // DB에서 NOT NULL + UNIQUE 제약 조건 → 중복 이메일 허용 안 함
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;
}

/*
1. User라는 클래스는 자바 객체이면서, 동시에 데이터베이스 테이블과 매핑되는 모델이 됨.
2. 엔티티는 DB 테이블과 직접 연결되는 자바 클래스를 의미. DTO 클래스는 DB와 매핑되지 않는 요청/응답만을 위해 쓰이는 것으로 엔티티x
3. 안정적 및 확장성 위해 getter/setter를 생성 * 추가) jpa의 리플렉션 : 프로그램이 자기자신을 들여다보고 내부구조(클래스, 메서드, 필드 등)에 접근할 수 있는 기능
4. JDBC는 쿼리 실행 후 Result Set 구조로 결과가 나옴. 하지만 여기서는 User객체가 필요. 이것을 JPA가 자동으로 처리해줌 (DB결과 원시데이터를 JPA가 USER객체로 자동 매핑)
5. GenerationType.IDENTITY = Auto Increment 사용함
6. nullable, unique, length, columnDefinition, precision/scale 등이 있음
*/
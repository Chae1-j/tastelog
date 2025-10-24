package com.tastelog.config;

import io.jsonwebtoken.*; // 토큰 생성/ 서명/ 검증/ 클레임 파싱 담당
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component // JWT 생성/검증/파싱 유틸 역할의 빈
public class JwtTokenProvider {

    private static final String ROLES = "roles"; // 권한 클레임의 키 이름을 상수로 관리
    private Key key;    // HMAC 서명/검증에 사용할 대칭 키.

    // 데모용 고정 시크릿 (운영에서는 환경변수/키관리로 주입)
    private final String secret = "tastelog-demo-secret-key-change-me-please-32bytes!";
    // 60분
    private final long validityInMs = 60 * 60 * 1000L;

    @PostConstruct  // 애플리케이션 시작 시 한 번만 시크릿 문자열로부터 key객체를 생성하여 캐싱.
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    /*
     초기화 구간. 애플리케이션 시작 시 1회 실행.
     HS256dyd 대칭키 객체를 미리 만들어 캐싱 > 매번 재생성 하지 않고 재사용 > 성능/일관성 확보
     
    */

    public String createToken(String subjectEmail, List<String> roles) { // 실제 토큰 생성, subjectEmail : 토큰 소유자 식별자. JWT의 sub 클레임에 저장, roles : 권한 목록. 커스텀 클레임 상수 ROLES로 저장
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityInMs);
        // now : 현재시각, exp : 만료 시각(60분 뒤)
        return Jwts.builder()
                .setSubject(subjectEmail)   // 토큰의 주체를 설정 ( 여기서는 이메일 소유자)
                .claim(ROLES, roles)        // 커스텀 권한 클레임 : JWT 표준 클레임 외에 개발자가 직접 정의한 데이터
                .setIssuedAt(now)           // 표준 클레임. iat: 토큰발급 시간.
                .setExpiration(exp)         // 표준 클레임. exp : 만료시각
                .signWith(key, SignatureAlgorithm.HS256) // 서명 생성 : header 와 payload 결합해, 지정한 알고리즘과 key로 서명
                .compact();                 // 직렬화(문자열 생성) 설정된 header, payload, signature를 결합하여 문자열로 변환
    }
    /*
    Jwts.builder()는 JWT 토큰을 구성학 위한 빌더 객체 생성. Header: 서명알고리즘, Payload: 토큰에 담을 데이터, Signature: Header + Payload를 시크릿 키로 서명한 값) 로 구성되는데 작성한 메서드들은 payload, signature를 채우는 단계
    Jwts.builder()는 JWT의 설계도를 작성하고,
    .setSubject()·.claim() 등으로 데이터를 담고,
    .signWith()로 위조방지 서명,
    .compact()로 전송 가능한 문자열로 직렬화하는 전 과정.
     */

    public boolean validate(String token) { // 토큰 검증. > 서명/ 형식/ 만료를 종합검증. 입력으로 JWT 문자열을받음.
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // jwt를 읽고 유효성 검증까지 수행하는 파서엔진을 만들기 위한 조립기. setSigningKey를 통해 어떤 키로 서명을 검증할지를 설정.
            /* .build() > 빌더에 입력된 설정을 적용한 JwtParser인스턴스를 만듦.
               .parseClaimsJws(token) 토큰을 실제로 디코딩 + 검증 + 클레임 파싱함.
               ExpiredJwtException : 토큰 만료
               SignatureException : 서명 불일치
               MalformedJwtException : 포맷 깨짐
               UnsupportedJwtException : 지원하지 않는 토큰
               IllegalArgumentException : 입력이 null/빈문자열 등
               등의 예외가 있음.
            */
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** (신규) 필터에서 사용할 이름 - validate와 동일 동작 */
    public boolean validateToken(String token) {
        return validate(token);
    }
    // >>>>> validate(token)를 그대로 호출하는 래퍼. 호출부와 구현부를 느슨하게 결합하여 추후에 검증 방식을 변경해도 validateToken내부만 변경하면 됨.

    /** (신규) Claims 공용 파서 */
    public Claims getClaims(String token) { // 토큰을 검증하면서 Payload(Claims) 부분만 꺼내옴
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        /*
        getBody()는 다음 JSON 내용(예)을 Claims 객체로 제공.
        {
          "sub": "user@example.com",
          "roles": ["ROLE_USER"],
          "iat": 1730379612,
          "exp": 1730383212
        }
        */
    }

    // 로그인한 사용자의 이메일을 바로 얻을 때 사용.
    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /** (신규) roles 클레임을 List<String>으로 반환 : 토큰에서 roles 클레임을 안전하게 꺼내어 문자열 리스트로 변환해줌 */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) { // jwt 클레임은 JSON을 파싱함 따라서 타입이 ArrayList<LinkedHashMap>이나 ArrayList<Object>로 전달될 가능성이 있음.
        Object raw = getClaims(token).get(ROLES);
        if (raw instanceof List<?>) {   // 리스트 타입 검사 후
            return ((List<?>) raw).stream().map(String::valueOf).collect(Collectors.toList()); // 내부 값을 String으로 변환하여 안전하게 매핑함
        }
        return Collections.emptyList();
    }
}

/*
JWT의 subject(sub)는 토큰이 누구 것인지를 나타내는 대표 식별자. 토큰 소유자를 가리키는 단 하나의 대표값.
 */

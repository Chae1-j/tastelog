package com.tastelog.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String ROLES = "roles";
    private Key key;

    // 데모용 고정 시크릿 (운영에서는 환경변수/키관리로 주입)
    private final String secret = "tastelog-demo-secret-key-change-me-please-32bytes!";
    // 60분
    private final long validityInMs = 60 * 60 * 1000L;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String subjectEmail, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(subjectEmail)
                .claim(ROLES, roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** (신규) 필터에서 사용할 이름 - validate와 동일 동작 */
    public boolean validateToken(String token) {
        return validate(token);
    }

    /** (신규) Claims 공용 파서 */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /** (신규) roles 클레임을 List<String>으로 반환 */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object raw = getClaims(token).get(ROLES);
        if (raw instanceof List<?>) {
            return ((List<?>) raw).stream().map(String::valueOf).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

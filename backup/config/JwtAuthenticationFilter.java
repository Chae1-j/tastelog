package com.tastelog.backup.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// > servlet 스펙 관련 타입.
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken; //스프링 시큐리티가 사용하는 인증 객체의 추상 베이스.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; //가장 일반적인 인증 객체 구현. 여기서는 JWT 검증 후 주체/권한을 담아 인증된 토큰으로 사용.
import org.springframework.security.core.context.SecurityContextHolder; // 현재 스레드(요청)에 대한 보안 컨텍스트 저장소. 여기에 인증 객체를 넣으면, 이후 컨트롤러/시큐리티 표현식에서 인증 정보 참조가능.
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 문자열 권한(예: "ROLE_USER")을 GrantedAuthority로 포장해주는 구현체.
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter 요청 하나당 정확히 한 번 실행되는 보안 필터.

    private final JwtTokenProvider jwtTokenProvider; // jwt 생성/검증/파싱 담당하는 유틸리티 빈 주입 받기

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException { // 서블릿 필터의 메인 진입점. HTTP 요청 시 스프링 키규리티 필터 체인에서 해당 메서드가 호출됨. chain은 다음 필터로 제어를 넘기는 핸들.

        // 이미 인증된 요청이면 패스(중복 셋팅 방지)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String bearer = request.getHeader("Authorization"); // HTTP 헤더에서 Authorization값을 읽음. 일반적으로 JWT는 Authorization: Bearer <토큰문자열> 형식으로 전달.
//        String token = null;
        String token = resolveToken(bearer); // 프리픽스 검사하고 제거.(단일책임 수행)

//        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
//            token = bearer.substring(7);
//        }
//
//        if (token != null && jwtTokenProvider.validate(token)) {
//            // 간단히 ROLE_USER 고정 (토큰에서 역할 파싱 확장 가능)
//            var auth = new UsernamePasswordAuthenticationToken(
//                    jwtTokenProvider.getSubject(token),
//                    null,
//                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
//            );
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 주체(subject): email 또는 userId (createToken 정책과 일치해야 함)
            String subject = jwtTokenProvider.getSubject(token);

            // roles 클레임 → SimpleGrantedAuthority 매핑
            List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(token).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            AbstractAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}

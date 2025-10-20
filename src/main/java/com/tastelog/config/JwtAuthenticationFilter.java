package com.tastelog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 이미 인증된 요청이면 패스(중복 셋팅 방지)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String bearer = request.getHeader("Authorization");
//        String token = null;
        String token = resolveToken(bearer);

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

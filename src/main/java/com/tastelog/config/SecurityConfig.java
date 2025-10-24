//package com.tastelog.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration  // 이 클래스가 스프링 설정 클래스임을 표시.
//public class SecurityConfig {
//
//    @Bean   // 해당 메서드가 반환하는 객체를 스프링 컨테이너에 등록
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 요청 권한 설정
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/users/**")) // CSRF 예외
//                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))        // H2 콘솔 프레임 허용
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/users/register", "/h2-console/**").permitAll()     // 회원가입, H2 콘솔은 모두 허용
//                        .anyRequest().authenticated()                                            // 나머지는 인증 필요
//                );
//        return http.build();
//    }
//}
//
///*
//1. 스프링 설정 클래스(@Configuration)의 의미
//- 스프링은 애플리케이션을 구성할 때 필요한 객체(Bean)들을 미리 준비
//- 설정 클래스 : 객체들이 어디서, 어떻게 생성되어야 하는지 알려주는 역할
//- @Configuration은 이 클래스 안에 스프링이 관리해야 할 Bean 생성방법이 있음을 의미
//
//2. 스프링 컨테이너(Bean Container)란?
//- 개발자가 new로 객체를 직접 만드는 대신, 스프링 컨테이너가 알아서 필요한 Bean을 생성하고 주입.
//
//
// */

package com.tastelog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 스프링 설정 클래스로 등록. @Bean의 메서드 들을 스프링 컨테이너에 등록
@EnableWebSecurity // 스프링 싴리티 웹 보안 기능 활성화. 필터체인 구성하게 해줌
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // csrf 공격 방식 방어하는 기능. 운영단계에서 활성화해야함
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // 모든 HTTP 요청의 단
//                );
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 안 쓰겠다고 선언
                .authorizeHttpRequests(auth -> auth
                        // 로그인 허용 (v1/비버전 둘 다 대비)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/auth/login").permitAll()
                        // 회원가입 허용 (POST /api/v1/users 또는 /api/users/register 사용 케이스 동시 허용)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/users/register").permitAll()
                        // (선택) refresh 도입 예정이면 미리 허용 토킅 재발급 주소 허용
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh", "/api/auth/refresh").permitAll()
                        // H2 콘솔 누구나 가능
                        .requestMatchers("/h2-console/**").permitAll()
                        // 그 외(나머지는 jwt있어야 접근 가능함) 보호
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // CORS 기본값 (필요 시 별도 Bean 제공)
                .cors(Customizer.withDefaults());

        http.headers(headers -> headers.frameOptions(frame-> frame.sameOrigin()));

        http.formLogin(f -> f.disable()).httpBasic(b -> b.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } // 비밀번호 암호화해서 저장/검증하기 위한 도구.
}

/*

* 프로젝트 전체의 보안 설정 중심축 (문단속 규칙표)

 */
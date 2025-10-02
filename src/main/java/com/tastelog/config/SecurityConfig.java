package com.tastelog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration  // 이 클래스가 스프링 설정 클래스임을 표시.
public class SecurityConfig {

    @Bean   // 해당 메서드가 반환하는 객체를 스프링 컨테이너에 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

/*
1. 스프링 설정 클래스(@Configuration)의 의미
- 스프링은 애플리케이션을 구성할 때 필요한 객체(Bean)들을 미리 준비
- 설정 클래스 : 객체들이 어디서, 어떻게 생성되어야 하는지 알려주는 역할
- @Configuration은 이 클래스 안에 스프링이 관리해야 할 Bean 생성방법이 있음을 의미

2. 스프링 컨테이너(Bean Container)란?
- 개발자가 new로 객체를 직접 만드는 대신, 스프링 컨테이너가 알아서 필요한 Bean을 생성하고 주입.


 */
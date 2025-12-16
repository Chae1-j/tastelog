package com.tastelog.common.response;

import org.springframework.http.HttpStatus;

// 응답코드(enum) 목록, 프로그램용 식별자 관리
public enum ApiCode {
    USER_REGISTERED(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "형식에 맞지않는 필드가 있습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT ,"이미 존재하는 이메일 주소입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 서버오류가 발생하였습니다");

    private final HttpStatus httpStatus;
    private final String message;

    ApiCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}

/*
-- 정리 --
enum 클래스는 맨 위에 상수들이 먼저 만들어져야 함. 상수 목록을 생성 후 값을 만들 때 사용할 필드/생성자를 작성

 */


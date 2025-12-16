package com.tastelog.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 응답데이터 자체(응답모양). 컨트롤러가 반환하는 객체
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private ApiCode code;
    private String message;
    private T data;
    private List<ErrorDetail> errors;
}

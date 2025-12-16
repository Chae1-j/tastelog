//package com.tastelog.common.response;
//
//// response를 만들어주는 도우미. 응답생성 메서드 모아둠
//public class ApiResponses {
//    // 성공일 때 응답
//    public static <T> ApiResponse<T> success(ApiCode code, T data) {
//        return ApiResponse.<T>builder()
//                .success(true)
//                .code(code)
//                .message(code.getMessage())
//                .data(data)
//                .errors(null)
//                .build();
//    }
//
//    // 실패일 때 응답
//    public static<T> ApiResponse<T> fail(ApiCode code, T data){
//        return ApiResponse.<T>builder()
//                .success(false)
//                .code(code)
//                .message(code.getMessage())
//                .data(data)
//                .errors(null)
//                .build();
//    };
//
//    public static<T> ApiResponse<T> validataionFail(ApiCode code, T data){
//        return ApiResponse.<T>builder()
//                .success(false)
//                .code(code)
//                .message(code.getMessage())
//                .data(data)
//                .errors(null)
//                .build();
//    };
//
//    private ApiResponses(){};
//
//}
package com.tastelog.common.response;

import java.util.List;

// response를 만들어주는 도우미. 응답생성 메서드 모아둠
public final class ApiResponses {

    // 성공일 때 응답
    public static <T> ApiResponse<T> success(ApiCode code, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(code.getMessage())
                .data(data)
                .errors(null)
                .build();
    }

    // 실패일 때 응답 (일반 실패: data 없음)
    public static ApiResponse<Void> fail(ApiCode code) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(code.getMessage())
                .data(null)
                .errors(null)
                .build();
    }

    // 검증 실패 응답 (필드별 errors 포함)
    public static ApiResponse<Void> validationFail(ApiCode code, List<ErrorDetail> errors) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(code.getMessage())
                .data(null)
                .errors(errors)
                .build();
    }

    private ApiResponses() {
    }
}

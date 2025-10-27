package com.tastelog.common.exception;

import jakarta.servlet.http.HttpServletRequest; // 사용자가 어떤 주소를 어떤 방식으로 요청했는지 등 요청 정보를 받기위해 필요.
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException; // 입력값 제약 조건을 어겼을 때 나오는 오류 타입.
import lombok.extern.slf4j.Slf4j;   // 로그를 남기기 위한 도구. 오류가 어디서 났는지를 기록함.
import org.springframework.http.HttpStatus; // http응답. 상태코드 모음
import org.springframework.http.ResponseEntity; // http응답. 상태코드 + 응답 본문
import org.springframework.http.converter.HttpMessageNotReadableException;  // JSON 오류
import org.springframework.security.access.AccessDeniedException;   // 권한 부족. 인가 실패
import org.springframework.security.core.AuthenticationException;   // 로그인 안될 떼. 인증 실패
import org.springframework.validation.FieldError;   // 입력값 검증 실패 시, 어떤 필드가 틀렸는지 담는 정보 단위
import org.springframework.web.HttpRequestMethodNotSupportedException; // 허용되지 않은 HTTP 방식
import org.springframework.web.bind.MethodArgumentNotValidException;    // @Valid 검증 실패 시
import org.springframework.web.bind.MissingServletRequestParameterException;    // 필수 파라미터 누락
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice; // 전역 오류 수거소로 지정
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;    // 시간을 기록
import java.util.LinkedHashMap; // 입력 순서 그대로 유지
import java.util.Map;
import java.util.stream.Collectors; // 목록을 맵으로 바꾸는 등의 데이터 묶기용

@Slf4j  // 로그 사용 가능
@RestControllerAdvice   // 전역 에러 컨트롤러로 선언
public class GlobalExceptionHandler {

    /**
     * 공통 응답 모델로 통일
     */
    private ResponseEntity<ErrorResponse> build(
            HttpServletRequest req, // 요청 정보(주소, 메서드)
            HttpStatus status,  // 상태코드(ex. 400, 401, 403, 500)
            String message, // 사용자에게 보여주는 핵심 에러 메시지
            Map<String, String> errors  // 필드별 오류 상세
    ) {
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now().toString(),
                req != null ? req.getMethod() : null,
                req != null ? req.getRequestURI() : null,
                status.value(),
                status.getReasonPhrase(),
                message,
                (errors == null || errors.isEmpty()) ? null : errors
        );
        return ResponseEntity.status(status).body(body);
        // ErrorResponse라는 표준 틀에 데이터 채움. ResponseEntity로 상태코드와 함께 반환 >>> 같은 JSON 구조로 내려감
    }

    /* =======================
     * 1) Validation(검증관련) 계열
     * ======================= */

    // @Valid @RequestBody - 필드 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class) // MethodArgumentNotValidException 타입의 오류가 발생하면 이 메서드로 처리.
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        // ex: 검증 실패 예외 자체, req: 사용자가 보낸 요청의 기본 정보 >> 출력 : 상태코드 + 응답본문(ErrorResponse).
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()   // 필드별 오류만 모아둔 목록을 가져옴
                .stream()   // 목록을 하나씩 처리
                .collect(Collectors.toMap(  // 리스트 > map으로 변환
                        FieldError::getField,   // map의 '키' : 어떤 필드?
                        FieldError::getDefaultMessage,  // map의 '값' : 오류 원인
                        (oldV, newV) -> newV, // 중복 키 충돌시 > 마지막 메시지로 덮어 쓰기
                        LinkedHashMap::new  // 입력순서 유지하는 map
                ));
        log.warn("Validation error: {}", fieldErrors, ex);  // 경고 로그. warn : 심각한 장애는 아니지만 주의가 필요한 상황
        //"{}" 자리에 fieldErrors가 깔끔하게 찍히고, ex를 함께 넘겨 스택트레이스(어디서 났는지 경로)도 기록
        return build(req, HttpStatus.BAD_REQUEST, "요청 데이터가 유효하지 않습니다.", fieldErrors);
    }

    // @Validated (@RequestParam, @PathVariable) 등 제약 위반
    @ExceptionHandler(ConstraintViolationException.class)   // 파라미터, 경로 변수 유효성 위반 시 400
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest req) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        cv -> extractProperty(cv),
                        ConstraintViolation::getMessage,
                        (oldV, newV) -> newV,
                        LinkedHashMap::new
                ));
        log.warn("Constraint violation: {}", errors, ex);
        return build(req, HttpStatus.BAD_REQUEST, "요청 파라미터가 유효하지 않습니다.", errors);
    }

    // 이 메서드는 유효성 오류의 위치 이름을 깔끔하게 정리해주는 도우미
    private String extractProperty(ConstraintViolation<?> cv) { // ConstraintViolation : 유효성 검사 위반. <?> : 타입 신경 안써도 됨.
        // 예: "create.userId" → 마지막 노드만 추출
        String path = cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : "";
        /*
        - cv.getPropertyPath()는 “어느 필드에서 문제가 났는지”
        - 만약 경로가 있다면 → 그걸 문자열로 바꾸기 (toString()), 경로가 없다면 → 빈 문자열("")을 대신 쓰기.
        - path 변수에는 "create.userId" 또는 "email" 또는 "" (비어 있음) 중 하나가 저장
         */
        int dot = path.lastIndexOf('.'); // 이 줄은 문자열 안에서 마지막 점('.')이 어디에 있는지 찾음
        return dot >= 0 ? path.substring(dot + 1) : path;
    }

    /* =======================
     * 2) 요청/메시지 포맷 계열
     * ======================= */

    @ExceptionHandler(HttpMessageNotReadableException.class)    // JSON 파싱 불가 400
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest req) {
        log.warn("Malformed JSON body", ex);
        return build(req, HttpStatus.BAD_REQUEST, "요청 바디(JSON) 형식이 올바르지 않습니다.", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)    // 타입불일치(자료형 불일치 등) 400
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(String.valueOf(ex.getName()), "타입이 올바르지 않습니다. value=" + ex.getValue());
        log.warn("Type mismatch: {}", errors, ex);
        return build(req, HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다.", errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)    // 필수 파라미터 누락 400
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(ex.getParameterName(), "필수 파라미터가 누락되었습니다.");
        log.warn("Missing request parameter: {}", errors, ex);
        return build(req, HttpStatus.BAD_REQUEST, "요청 파라미터가 누락되었습니다.", errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // 허용되지 않은 HTTP 메서드 405
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest req) {
        log.warn("Method not supported: {}", ex.getMethod(), ex);
        return build(req, HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다.", null);
    }

    /* =======================
     * 3) 비즈니스/도메인 계열
     * ======================= */

    @ExceptionHandler(IllegalArgumentException.class)   // 잘못된 인자/입력. 400. 업무규칙상 허용되지 않는 값 > 이메일 중복 등
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                               HttpServletRequest req) {
        log.warn("Illegal argument: {}", ex.getMessage(), ex);
        return build(req, HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalStateException.class)  // 처리 불가 상태. 현재 상태에서 할 수 없는 동작을 시도. ex) 이미 취소된 주문을 또 취소. 409
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex,
                                                            HttpServletRequest req) {
        log.warn("Illegal state: {}", ex.getMessage(), ex);
        return build(req, HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    /* =======================
     * 4) 보안(JWT) 계열
     * ======================= */

    @ExceptionHandler(AuthenticationException.class)    // 인증실패 : 로그인/토큰문제 401. 보안상 상세사유 노출 x
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex,
                                                    HttpServletRequest req) {
        log.warn("Authentication failed: {}", ex.getMessage(), ex);
        return build(req, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", null);
    }

    @ExceptionHandler(AccessDeniedException.class)  // 인가실패 : 권한 부족 403.
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest req) {
        log.warn("Access denied: {}", ex.getMessage(), ex);
        return build(req, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", null);
    }

    /* =======================
     * 5) 최종 Fallback
     * ======================= */

    @ExceptionHandler(Exception.class)  // 예기치 못한 오류. 500
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", null);
    }

    /**
     * 에러 응답 표준 모델 (필요 시 별도 파일로 분리 가능)
     */
    public static final class ErrorResponse {
        private final String timestamp;	// ISO-8601
        private final String method;	// GET/POST...
        private final String path;		// /api/...
        private final int status;		// 400, 401...
        private final String error;		// BAD_REQUEST 등
        private final String message;	// 사람이 읽을 메시지
        private final Map<String, String> errors; // 필드별 오류(선택)

        public ErrorResponse(String timestamp, String method, String path,
                             int status, String error, String message,
                             Map<String, String> errors) {
            this.timestamp = timestamp;
            this.method = method;
            this.path = path;
            this.status = status;
            this.error = error;
            this.message = message;
            this.errors = errors;
        }

        public String getTimestamp() { return timestamp; }
        public String getMethod() { return method; }
        public String getPath() { return path; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public Map<String, String> getErrors() { return errors; }
    }
}

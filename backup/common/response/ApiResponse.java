package com.tastelog.backup.common.response;

import java.time.OffsetDateTime;    // 타임존 오프셋을 포함한 시간 표현
import java.time.ZoneOffset; // UTC 오프셋 상수 제공
import java.util.Objects;   // equals, hashCode 구현 시 Objects.equals, Objects.hash를 사용하기 위함

/**
 * 공통 응답 포맷
 * 성공/실패를 단일 형태로 통일합니다.
 * Controller에서는 보통 ResponseEntity.ok(ApiResponse.ok(data)) 형태로 사용합니다.
 */
public final class ApiResponse<T> { // final > 상속금지. 응답 스키마 안정성과 불변 패턴을 유지. T > data에 담길 실제 페이로드 타입을 의미. 즉, 응답의 data 필드가 어떤 타입이 될지 외부에서 지정 가능하다”는 뜻
    private final boolean success;  // 요청처리 성공여부. final로 immutable
    private final int status;   // http 상태코드 그대로 담음
    private final String message;
    private final T data;//  실제 응답 본문.
    private final String timestamp;

    private ApiResponse(boolean success, int status, String message, T data) {
        // private 생성자. 외부에서는 new로 직접 생성하지 못하게 막고, 아래의 정적 팩토리 메서드만 통해 만들도록 강제

        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString();
    }

    public static <T> ApiResponse<T> ok(T data) { // 성공 응답
        return new ApiResponse<>(true, 200, "OK", data);
    }

    public static <T> ApiResponse<T> created(T data) {  // 생성 성공 응답. 리소스 생성 api에서 사용
        return new ApiResponse<>(true, 201, "CREATED", data);
    }

    public static ApiResponse<Void> noContent() {   // 내용 없음 응답 팩토리. 삭제/갱신 등 바디가 필요 없는 성공 응답 시 사용.
        return new ApiResponse<>(true, 204, "NO_CONTENT", null);
    }

    public static <T> ApiResponse<T> fail(int status, String message) {
        return new ApiResponse<>(false, status, message, null);
    }

    public static <T> ApiResponse<T> of(boolean success, int status, String message, T data) {
        return new ApiResponse<>(success, status, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) { // 기본 주소 비교 대신, 내용 비교로 바꾸기 위해 Object.equals 오버라이딩
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse<?> that = (ApiResponse<?>) o;
        return success == that.success
                && status == that.status
                && Objects.equals(message, that.message)
                && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        /*
         hashCode()는 객체를 숫자 값으로 표현하는 메서드. HashMap, HashSet, HashTable 같은 자료구조에서 객체를 빠르게 찾기 위해 사용
         equals와 함께 항상 짝으로 재정의 해야함. 아니면 HashMap/Set에서 정상 동작하지 않음
         */
        return Objects.hash(success, status, message, data);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

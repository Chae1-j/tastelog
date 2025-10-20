# tastelog
Personal side project - Taste Log (Spring boot + React)



---

## API 버전 및 기본 경로

- Base URL: `/api/v1`
- 인증: `/api/v1/auth/*`
- 사용자: `/api/v1/users*`

---

## 엔드포인트 스펙

### 1) 인증 (`auth`)

| 메서드 | 경로                      | 인증 | 요청 바디 DTO                         | 응답 DTO                           | 상태 | 설명 |
|------|-------------------------|----|------------------------------------|-----------------------------------|----|-----|
| POST | `/auth/login`          | 불필요 | `LoginRequest { email, password }`   | `LoginResponse { accessToken, tokenType }` | 200 | 이메일/비밀번호 검증 후 JWT 발급 |
| POST | `/auth/refresh`        | 불필요(리프레시 보유) | `TokenRefreshRequest { refreshToken }` 또는 HttpOnly 쿠키 | `LoginResponse` | 200 | refresh로 access 재발급 |
| POST | `/auth/logout`         | 필요(또는 refresh 보유) | (옵션)                             | `ApiResponse`                      | 200 | 서버 저장 refresh 무효화·쿠키 만료 등 |
| GET  | `/auth/check` (선택)    | 필요 | -                                  | `AuthCheckResponse { userId, email, roles }` | 200 | 토큰 유효성 확인용 |

**오류 상태 권장 맵핑**  
- 400 Bad Request: 요청 형식/필드 오류  
- 401 Unauthorized: 자격 증명 실패, 토큰 만료/위조  
- 403 Forbidden: 권한 부족

---

### 2) 사용자 (`user`)

| 메서드 | 경로           | 인증 | 요청 바디 DTO                                              | 응답 DTO                                   | 상태 | 설명 |
|------|--------------|----|-----------------------------------------------------------|--------------------------------------------|----|-----|
| POST | `/users`     | 불필요 | `UserRegisterRequest { email, password, nickname }`        | `UserRegisterResponse { userId, email, nickname }` | 201 | 회원가입(중복 이메일 체크, 비밀번호 해시 저장) |
| GET  | `/users/me`  | 필요 | -                                                         | `UserProfileResponse { userId, email, nickname, roles }` | 200 | 내 정보 조회(토큰 주체) |
| PATCH| `/users/me`  | 필요 | `UserUpdateRequest { nickname?, password? }`               | `UserProfileResponse`                       | 200 | 내 정보 수정(닉네임·비밀번호 등) |
| DELETE | `/users/me`| 필요 | -                                                         | `ApiResponse`                               | 200 | 회원 탈퇴(정책에 따라 소프트/하드 삭제) |

**오류 상태 권장 맵핑**  
- 400 Bad Request: 유효성 검증 실패  
- 404 Not Found: 리소스 없음  
- 409 Conflict: 이메일 중복 등 고유 제약 충돌

---

## 보안/시큐리티 설정 요약

- 세션: `STATELESS`  
- CSRF: 비활성(JWT 기반일 때)  
- CORS: 프론트 도메인 허용(Origin/Methods/Headers/Credentials)  
- 허용 경로(`permitAll`):
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/refresh`
  - `POST /api/v1/users` (회원가입)
- 그 외 경로: `authenticated()`  
- JWT 필터: `UsernamePasswordAuthenticationFilter` **이전**에 등록  
- JWT 권장 클레임:  
  - `sub`: 변경 불가능한 `userId`  
  - `email`: 사용자 이메일(선택)  
  - `roles`: `["ROLE_USER", ...]`  
  - `iat`, `exp`, `iss`

---

## 응답 포맷 가이드 (권장)

- 성공
  ```json
  { "code": "OK", "message": "", "data": { ... } }

1. 경계 분리: auth(인증/토큰), user(사용자 도메인) 책임을 명확히 유지합니다.

2. DTO 네이밍 일관성:

  auth: LoginRequest/Response, TokenRefreshRequest

  user: UserRegisterRequest/Response, UserProfileResponse, UserUpdateRequest

3. 레이어드 아키텍처:

  Controller = 입출력/검증, Service = 비즈니스 로직, Repository = 데이터 접근.

4. Validation 우선: DTO에 @Valid + @NotBlank, @Email, @Size 등 적용. 컨트롤러 파라미터에 @Valid 필수.

5. 예외/상태코드 통일: 전역 @RestControllerAdvice로 400/401/403/404/409 매핑을 일관화합니다.

6. 보안 키/설정 분리: JWT 키·만료·issuer는 application.yml 또는 환경 변수에서 주입(하드코딩 금지).

7. 식별자 원칙: JWT sub에는 가급적 변경 불가능한 userId 사용, 이메일은 별도 클레임으로.

8. 트랜잭션 규칙: 조회는 @Transactional(readOnly = true), 생성/수정/삭제는 @Transactional.

9. 로그/민감정보: 비밀번호/토큰 원문은 로그에 남기지 않음. 실패 사유는 서버 로그에 상세, 응답은 모호하게.

10. 버전/경로 합의: /api/v1 접두사 유지, 인증 /auth/*, 사용자 /users* 경로 체계 준수.

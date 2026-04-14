# WorkIt Server

2025 호주 글로벌 인턴십 프로젝트 — 구직자와 구인자를 연결하는 구인구직 플랫폼 **WorkIt**의 백엔드 API 서버입니다.

Swagger UI: [https://api-workit.mmhs.app/swagger-ui/index.html](https://api-workit.mmhs.app/swagger-ui/index.html)

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.9 |
| Build | Gradle 8.14.3 |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (HS256) |
| Realtime | Spring WebSocket (STOMP + SockJS) |
| API 문서 | SpringDoc OpenAPI (Swagger) |
| 배포 | Docker, Docker Compose |

---

## 주요 기능

### 인증
- 이메일 회원가입 / 로그인
- Google OAuth (ID Token 검증)
- Apple 로그인 (준비 중)
- JWT 발급 및 필터 기반 인증
- 회원가입 후 역할 선택 (구직자 / 구인자)

### 채용 공고
- 인기 공고 / 최신 공고 / 지역별 / 장단기 필터링
- 키워드 검색
- 위경도 기반 반경 내 근처 공고 조회 (Haversine)
- 공고 상세 조회 (지원자 수, 북마크 여부 포함)

### 지원 및 북마크
- 공고 지원 (커버레터, 연락처 포함)
- 지원 내역 조회 / 취소
- 공고 북마크 추가 / 삭제 / 목록 조회

### 이력서
- 이력서 생성 / 조회 / 수정 / 삭제
- 파일 URL 첨부 지원

### 채팅
- WebSocket(STOMP)을 활용한 실시간 채팅
- 채팅방 생성(공고 기준) / 목록 조회 / 메시지 목록 조회
- 읽음 처리 및 미읽음 카운트

### 알림
- 알림 목록 조회 / 읽음 처리 / 전체 읽음 / 삭제

### 이미지 업로드
- Multipart 파일 업로드 및 Base64 업로드 지원
- 서버 로컬 저장 후 URL 반환

---

## 프로젝트 구조

```
src/main/java/com/jubilee/workit/
├── config/         # Security, JWT, WebSocket, Swagger 설정
├── controller/     # REST 컨트롤러 및 WebSocket 핸들러
├── dto/            # 요청/응답 DTO
├── entity/         # JPA 엔티티
├── repository/     # Spring Data JPA 레포지토리
├── security/       # JWT 인증 필터
├── service/        # 비즈니스 로직
└── util/           # JwtUtil
```

---

## DB 설계

### ERD 요약

```
users ──────────────────────────────────────────────┐
  │                                                  │
  ├── (location_id) ──→ locations ←── job_postings  │
  │                                        │         │
  ├── notifications                        └── job_posting_categories ──→ categories
  │
  ├── bookmarks ──→ job_postings
  │
  ├── applications ──→ job_postings
  │
  ├── resume (1:1)
  │
  └── chat_rooms (employer / applicant) ──→ job_postings
        └── chat_messages
```

### 테이블 명세

**users**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | |
| email | VARCHAR(255) UNIQUE | |
| password | VARCHAR(255) | 소셜 로그인 시 NULL |
| role | VARCHAR(20) | JOBSEEKER / EMPLOYER |
| login_type | VARCHAR(20) | EMAIL / GOOGLE / APPLE |
| google_id | VARCHAR(255) UNIQUE | |
| apple_id | VARCHAR(255) UNIQUE | |
| location_id | BIGINT FK → locations | |
| preferences | JSON | |
| created_at, updated_at | DATETIME | |

**locations**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(255) UNIQUE | |
| city | VARCHAR(255) | |
| country | VARCHAR(255) | |
| latitude | DECIMAL(10,8) | |
| longitude | DECIMAL(11,8) | |

**companies**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(255) UNIQUE | |
| description | TEXT | |
| logo_url | VARCHAR(512) | |
| website_url | VARCHAR(512) | |
| created_at, updated_at | DATETIME | |

**job_postings**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | |
| title | VARCHAR(255) | |
| description | TEXT | |
| company_id | BIGINT FK → companies | |
| location_id | BIGINT FK → locations | |
| employer_id | BIGINT FK → users | |
| compensation_amount | DECIMAL(12,2) | |
| compensation_type | VARCHAR(20) | HOURLY / YEARLY |
| job_type | VARCHAR(20) | FULL_TIME / PART_TIME / CASUAL |
| duration_type | VARCHAR(20) | LONG_TERM / SHORT_TERM |
| image_url | VARCHAR(512) | |
| is_hot | TINYINT(1) | |
| published_at, expires_at | DATETIME | |
| created_at, updated_at | DATETIME | |

**categories** / **job_posting_categories** (N:M)

**applications**

| 컬럼 | 설명 |
|------|------|
| user_id, job_posting_id | FK |
| cover_letter | TEXT |
| status | PENDING / REVIEWING / ACCEPTED / REJECTED / WITHDRAWN |
| applied_at, updated_at | DATETIME |

**bookmarks** — user_id + job_posting_id UNIQUE

**resumes** — user_id UNIQUE (1:1)

**notifications** — user_id, message, type, is_read

**chat_rooms** — job_posting_id, employer_id, applicant_id

**chat_messages** — chat_room_id, sender_id, message, is_read

---

## 로컬 실행

```bash
# 1. 환경 변수 설정 (application.yaml 또는 환경변수)
# - spring.datasource.url/username/password
# - workit.jwt.secret (256비트 이상)
# - workit.google.client-id

# 2. 빌드 및 실행
./gradlew bootRun

# 또는 Docker
docker-compose up -d
```

기동 후 Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## API 인증

모든 인증 필요 엔드포인트는 `Authorization: Bearer {token}` 헤더를 사용합니다.

토큰은 `/api/auth/signup`, `/api/auth/login`, `/api/auth/google` 응답의 `accessToken` 필드로 발급됩니다.

---

## 테스트

```bash
./gradlew test
```

- `WorkitApplicationTests` — 스프링 컨텍스트 로드 테스트
- `JwtUtilTest` — JWT 생성/파싱/키 길이 검증 테스트

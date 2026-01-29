# 💼 WorkIt Server

2025 호주 글로벌 인턴십 프로젝트  
**구인구직 플랫폼 WorkIt의 백엔드 API 서버**

---

## 📋 프로젝트 개요

**WorkIt**은 구직자와 구인자를 연결하는 구인구직 플랫폼의 백엔드 서버입니다.  
Spring Boot 기반으로 개발되었으며, RESTful API를 통해 인증, 채용 공고 관리, 지역 설정, 알림 기능 등을 제공합니다.

---

## 🛠 기술 스택

| 구분 | 기술 |
|---|---|
| Framework | Spring Boot 3.5.9 |
| Language | Java 17 |
| Build Tool | Gradle 8.14.3 |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Deployment | Docker |

---

## 🚀 주요 기능

### 1️⃣ 인증 (Authentication)
- 이메일 회원가입 / 로그인
- Google OAuth 로그인
- Apple 로그인 (준비 중)
- JWT 기반 인증
- 사용자 역할 선택 (구직자 / 구인자)

### 2️⃣ 채용 공고 (Job Postings)
- 인기 공고 조회
- 최신 공고 조회
- 지역별 필터링
- 장기 / 단기 필터링
- 검색 기능
- 페이징 처리

### 3️⃣ 지역 관리 (Locations)
- 지역 목록 조회
- 사용자 선호 지역 설정

### 4️⃣ 알림 (Notifications)
- 사용자별 알림 조회
- 페이징 처리

---

## 📁 프로젝트 구조

```text
src/main/java/com/jubilee/workit/
├── config/              # 설정 클래스
│   ├── JwtProperties.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/          # REST 컨트롤러
│   ├── AuthController.java
│   ├── JobController.java
│   ├── LocationController.java
│   ├── NotificationController.java
│   └── UserController.java
├── dto/                 # 데이터 전송 객체
├── entity/              # JPA 엔티티
│   ├── User.java
│   ├── JobPosting.java
│   ├── Company.java
│   ├── Location.java
│   ├── Category.java
│   └── Notification.java
├── repository/          # JPA 리포지토리
├── security/            # 보안 관련
│   └── JwtAuthFilter.java
├── service/             # 비즈니스 로직
└── util/                # 유틸리티
    └── JwtUtil.java

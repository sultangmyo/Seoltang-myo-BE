# 설탕묘 (Seoltang-myo) Backend

설탕묘 프로젝트의 백엔드 서버입니다.

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.5 |
| Build Tool | Gradle |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL (운영), H2 (테스트) |
| Etc | Lombok, Spring DevTools |

---

## 프로젝트 구조

```
sugarcat/
├── build.gradle                  # 빌드 및 의존성 설정
├── settings.gradle
├── gradlew
├── gradle/
│   └── wrapper/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/seoltangmyo/sugarcat/     # 패키지 구조 (하단 참고)
    │   └── resources/
    │       ├── application.yml               # 공통 설정
    │       ├── application-local.yml         # 로컬 환경 설정
    │       ├── application-dev.yml           # 개발 환경 설정
    │       └── application-secret.yml        # 시크릿 설정 (git 제외)
    └── test/
        └── java/
            └── com/seoltangmyo/sugarcat/
                └── SugarcatApplicationTests.java
```

### 패키지 구조

```
com.sultangmyo.api
├── global/
│   ├── auth/                  # JWT, 소셜 로그인 (Apple, Kakao)
│   │   ├── JwtProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── dto/
│   ├── config/                # SecurityConfig, WebConfig, SwaggerConfig
│   ├── common/                # BaseEntity(createdAt, updatedAt), ApiResponse 등
│   ├── error/                 # GlobalExceptionHandler, ErrorCode enum, CustomException
│   └── util/                  # InviteCodeGenerator 등 유틸
│
├── domain/
│   ├── user/                  # 사용자 (소셜 로그인, 온보딩)
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │       ├── request/
│   │       └── response/
│   │
│   ├── cat/                   # 고양이 프로필
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   ├── health/                # 혈당 기록, 건강 데이터
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   ├── graph/                 # 혈당 그래프 (일간/주간/월간)
│   │   ├── controller/
│   │   ├── service/
│   │   └── dto/
│   │
│   ├── routine/               # 루틴 (로컬 알림용 시간 데이터)
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   └── invite/                # 초대코드
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       └── dto/
│
└── SultangmyoApplication.java
```

---

## 환경 설정

### 프로파일

- `local` : 로컬 개발 환경 (기본값)
- `dev` : 개발 서버 환경
- `secret` : 민감 정보 (DB 계정 등, git에 포함하지 않음)

### 서버 포트

```
8080
```

---

## 실행 방법

```bash
# 빌드
./gradlew build

# 실행 (로컬)
./gradlew bootRun

# 테스트
./gradlew test
```

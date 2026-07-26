# Sugarcat Server

**설탕묘** 서비스의 Spring Boot Backend Server입니다.


## Overview

설탕묘는 당뇨 고양이의 혈당, 식사, 인슐린 기록과 관리 일정을 공유하고 함께 관리할 수 있는 서비스입니다.

---
## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL 16
- Docker / Docker Compose
- AWS EC2 / ECR
- Nginx
- APNs (Pushy)

## Architecture

```text
iOS App
    │
 HTTPS
    │
 Nginx
    │
Spring Boot
    │
PostgreSQL
```

## Project Structure

```text
.
└── sugarcat
    ├── SugarcatApplication.java
    ├── domain
    │   ├── auth
    │   ├── bloodsugar
    │   ├── cache
    │   ├── cat
    │   ├── insulin
    │   ├── meal
    │   ├── notice
    │   ├── notification
    │   ├── schedule
    │   ├── statistic
    │   └── user
    └── global
        ├── config
        ├── entity
        ├── error
        └── security
```

프로젝트는 도메인 중심으로 구성되어 있습니다.

- `domain`: 도메인별 비즈니스 로직
- `global`: 프로젝트 전역 공통 모듈
- `config`: Spring Security, Swagger, APNs 등 애플리케이션 설정
- `security`: 인증 및 JWT 처리

---

## Current Version

- `v1.0.0`

## Version

버전별 변경 사항은 [`CHANGELOG.md`](./CHANGELOG.md)에서 관리합니다.
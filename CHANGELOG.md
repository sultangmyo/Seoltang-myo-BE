# 📝 Changelog

프로젝트의 주요 변경 사항을 버전별로 관리합니다.

---

## [Unreleased]

### ✨ Added

-

### 🔄 Changed

-

### 🐛 Fixed

-

### 🗄 Database

-

---

## [1.1.0] - 2026-10-01

### ✨ Added

- Spring Boot Actuator 기반 애플리케이션 상태 및 메트릭 수집
- Prometheus / Grafana 기반 서버 모니터링 환경 구축
- JVM, HTTP 요청, DB Connection Pool 등 주요 지표 모니터링

### 🔄 Changed

- Docker Compose에 Prometheus / Grafana 서비스 추가
- Git commit SHA 기반 Docker 이미지 관리 및 릴리스 버전 관리 개선
- 문서 변경 시 불필요한 자동 배포 제외

---

## [1.0.0] - 2026-07-26

### ✨ Added

- Initial production release
- APNs Production 지원
- Docker / ECR 운영 환경 구성
- Docker 이미지 버전 관리 적용

### 🗄 Database

- 중복 인덱스 제거
- `V1.0.0__remove_redundant_indexes.sql` 추가
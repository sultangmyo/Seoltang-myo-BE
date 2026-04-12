# =========================
# 1. Build Stage
# =========================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

COPY src ./src

# 테스트 제외하고 실행
RUN ./gradlew clean bootJar -x test --no-daemon


# =========================
# 2. Runtime Stage
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
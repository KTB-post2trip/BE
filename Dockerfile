# 🟢 Stage 1: Build Stage (멀티 스테이지 빌드)
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# 🔹 1. Gradle Wrapper 실행 권한 부여
COPY gradlew gradlew
RUN chmod +x gradlew

# 🔹 2. 의존성 캐싱 (Gradle)
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

# 🔹 3. 애플리케이션 소스 복사 및 빌드
COPY . .
RUN ./gradlew build --no-daemon

# 🟢 Stage 2: Runtime Stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# 🔹 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 🔹 보안 강화: ARG를 사용하여 민감한 정보 전달
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG KAKAO_API_KEY
ARG KAKAO_REDIRECT_URL
ARG AI_SERVER_URL
ARG SWAGGER_URL

# 🔹 실행 환경 변수 전달 (보안 강화)
ENV DB_URL=$DB_URL \
    DB_USERNAME=$DB_USERNAME \
    DB_PASSWORD=$DB_PASSWORD \
    KAKAO_API_KEY=$KAKAO_API_KEY \
    KAKAO_REDIRECT_URL=$KAKAO_REDIRECT_URL \
    AI_SERVER_URL=$AI_SERVER_URL \
    SWAGGER_URL=$SWAGGER_URL

# 🔹 애플리케이션 포트 노출
EXPOSE 8080

# 🔹 컨테이너 실행 명령어
CMD ["java", "-jar", "/app/app.jar"]

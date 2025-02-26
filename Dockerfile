# 🟢 Stage 1: Build Stage
FROM openjdk:17-jdk-slim AS build

# 필요한 패키지 설치 및 시간대 설정 (레이어 최적화)
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 🟢 Gradle 캐시 최적화: 의존성 먼저 복사하여 변경되지 않으면 캐싱 활용
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 🟢 소스 코드 복사 및 빌드 수행 (테스트 제외)
COPY src /app/src
RUN ./gradlew build -x test --no-daemon

# 🟢 Stage 2: Runtime Stage
FROM openjdk:17-jdk-slim

# 시간대 설정
ENV TZ=Asia/Seoul

WORKDIR /app

# 🟢 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 🟢 환경변수 설정 (GitHub Actions → Docker Build에서 ARG로 전달)
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG KAKAO_API_KEY
ARG KAKAO_REDIRECT_URL
ARG AI_SERVER_URL

# 🟢 컨테이너 환경변수 설정
ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD
ENV KAKAO_API_KEY=$KAKAO_API_KEY
ENV KAKAO_REDIRECT_URL=$KAKAO_REDIRECT_URL
ENV AI_SERVER_URL=$AI_SERVER_URL

# 애플리케이션 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션 실행
CMD ["java", "-jar", "/app/app.jar"]

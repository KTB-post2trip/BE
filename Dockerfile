# 🟢 Stage 1: Build Stage
FROM openjdk:17-jdk-slim AS build

WORKDIR /app
# 🔹 2. 의존성 캐싱 레이어 (Gradle)
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

# 🔹 3. 애플리케이션 소스 복사 및 빌드
COPY . .
RUN ./gradlew build --no-daemon

# 🟢 Stage 2: Runtime Stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# 🟢 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 🟢 컨테이너 환경변수 설정
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV KAKAO_API_KEY=${KAKAO_API_KEY}
ENV KAKAO_REDIRECT_URL=${KAKAO_REDIRECT_URL}
ENV AI_SERVER_URL=${AI_SERVER_URL}
ENV SWAGGER_URL=${SWAGGER_URL}

# 애플리케이션 포트 노출
EXPOSE 8080
# 🔹 7. 컨테이너 실행 명령어
CMD ["java", "-jar", "app.jar"]
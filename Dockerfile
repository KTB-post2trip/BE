# Stage 1: Build Stage
FROM openjdk:17-jdk-slim AS build

# 필요한 패키지 설치와 시간대 설정을 한 번에 실행하여 레이어 수를 줄임
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Gradle Wrapper와 빌드 스크립트 복사 (캐시 활용을 위해 소스 복사 이전에 수행)
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# 소스 코드 복사
COPY src /app/src

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 테스트를 제외한 빌드 수행 (빌드 결과물은 build/libs/ 하위에 생성됨)
RUN ./gradlew build -x test

# Stage 2: Runtime Stage
FROM openjdk:17-jdk-slim

# 필요 시 시간대 환경 변수 설정 (실행 시 TZ 설정)
ENV TZ=Asia/Seoul

WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사 (하나의 JAR 파일이 있다고 가정)
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션 실행
CMD ["java", "-jar", "/app/app.jar"]

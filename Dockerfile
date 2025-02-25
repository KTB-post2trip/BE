# 베이스 이미지 (예: OpenJDK 17 사용)
FROM openjdk:17-jdk-alpine

# 어플리케이션 JAR 파일을 복사 (빌드 결과물 target 폴더 내 JAR 파일)
COPY target/your-app.jar app.jar

# 임시 파일 저장소 볼륨 (옵션)
VOLUME /tmp

# 애플리케이션 포트 노출 (필요에 따라 조정)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

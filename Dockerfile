# Base image로 OpenJDK 17 선택
FROM openjdk:17-jdk-alpine

RUN apk --no-cache add tzdata
ENV TZ=Asia/Seoul

# Set the working directory
WORKDIR /app

# Copy the Spring Boot application WAR file to the container
COPY ./build/libs/greenlight-thehyundai-sample-0.0.1-SNAPSHOT.jar /app/greenlight-thehyundai-sample.jar

# 애플리케이션 포트 열기 18080: Spring
EXPOSE 38080

# JAR 파일을 실행하도록 ENTRYPOINT 설정
ENTRYPOINT ["java", "-jar", "greenlight-thehyundai-sample.jar"]
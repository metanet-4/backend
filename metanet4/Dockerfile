# 1단계: Maven을 사용해 Spring Boot 애플리케이션 빌드
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# 프로젝트 루트 경로 설정
WORKDIR /app

# metanet4 프로젝트 전체 복사
COPY ./ .

# 빌드 실행 (테스트는 건너뜀)
RUN mvn clean package -DskipTests

# 2단계: Tomcat 기반 이미지
FROM tomcat:10.1-jdk17

# (1) apt-get 설치 시 tzdata 설정 과정에서 자동 입력 방지
ENV DEBIAN_FRONTEND=noninteractive
# (2) 컨테이너 기본 타임존 설정
ENV TZ=Asia/Seoul
# (3) JVM 옵션으로 타임존 설정 (Oracle 에러 방지)
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

# (4) tzdata 설치 & 불필요 캐시 제거
RUN apt-get update && apt-get install -y tzdata \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# (5) 기존 ROOT.war 제거
RUN rm -rf /usr/local/tomcat/webapps/ROOT*

# (6) 빌드된 WAR 파일 복사
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# 포트 노출
EXPOSE 8080

# Tomcat 실행
CMD ["catalina.sh", "run"]


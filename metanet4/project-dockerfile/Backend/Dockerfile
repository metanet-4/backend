# 1단계: Maven을 사용해 Spring Boot 애플리케이션 빌드
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# 프로젝트 루트 경로 복사
WORKDIR /app
COPY ./pom.xml ./ 
COPY ./src ./src

# 빌드 실행 (테스트는 건너뜀)
RUN mvn clean package -DskipTests

# 2단계: Tomcat 기반 이미지
FROM tomcat:10.1-jdk21

# 기존 ROOT.war 제거
RUN rm -rf /usr/local/tomcat/webapps/ROOT*

# 빌드된 WAR 파일 복사
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# 포트 노출
EXPOSE 8080

# Tomcat 실행
CMD ["catalina.sh", "run"]

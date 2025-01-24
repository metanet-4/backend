FROM tomcat:10.1-jdk21

# 기존 ROOT.war 제거 (필요 시)
RUN rm -rf /usr/local/tomcat/webapps/ROOT*

# WAR 파일 복사
COPY team4-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 포트 노출
EXPOSE 8080

# Tomcat 실행
CMD ["catalina.sh", "run"]

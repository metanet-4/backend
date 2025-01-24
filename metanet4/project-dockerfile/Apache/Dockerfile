FROM httpd:2.4

# 설정 파일들을 컨테이너로 복사
COPY httpd.conf /usr/local/apache2/conf/
COPY httpd-vhosts.conf /usr/local/apache2/conf/extra/

WORKDIR /usr/local/apache2
EXPOSE 80

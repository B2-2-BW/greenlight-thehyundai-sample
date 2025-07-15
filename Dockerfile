FROM tomcat:9.0-jdk17

# (선택) 기본 ROOT 앱 제거
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# 시간대 설정
ENV TZ=Asia/Seoul

# 빌드된 war 파일을 ROOT.war로 복사
COPY ./build/libs/thehyundaisample-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 톰캣 실행
CMD ["catalina.sh", "run"]
FROM tomcat:11.0-jdk17

# 기본 ROOT 앱 제거 (선택)
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# 시간대 설정
ENV TZ=Asia/Seoul

# 최상위 context root(/)로 배포를 위한 ROOT.war 복사
COPY ./build/libs/thehyundaisample-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 톰캣 실행
CMD ["catalina.sh", "run"]
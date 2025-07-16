# greenlight-thehyundai-sample

## 프로젝트 개요

이 프로젝트는 Java Spring Boot 기반의 간단한 웹 애플리케이션 샘플입니다. 상품 검색, 상세 조회, 주문, 주문 완료까지의 기본적인 이커머스 흐름을 시뮬레이션합니다.

## 기술 스택

*   **언어**: Java 17
*   **프레임워크**: Spring Boot
*   **빌드 도구**: Gradle
*   **뷰 템플릿**: JSP (JavaServer Pages)
*   **서버**: 내장 Tomcat (Docker를 통해 Tomcat 11 컨테이너에 배포)

## 핵심 기능

*   **상품 흐름 시뮬레이션**:
    1.  `/search`: 상품 목록 페이지
    2.  `/itemPtc`: 상품 상세 페이지
    3.  `/order`: 주문서 페이지
    4.  `/orderComplete`: 주문 완료 페이지
*   **데이터**: 상품 정보는 `SampleController.java`에 하드코딩되어 있습니다.
*   **요청 로깅**: `SampleInterceptor`가 모든 웹 요청을 가로채 URL과 파라미터를 콘솔에 출력합니다.

## 실행 방법

1.  **Gradle을 사용하여 빌드**:
    ```bash
    ./gradlew build
    ```
2.  **Docker를 사용하여 실행**:
    ```bash
    docker build -t thehyundai-sample .
    docker run -p 38080:8080 thehyundai-sample
    ```
3.  브라우저에서 `http://localhost:38080/search` 로 접속합니다.

## 주요 파일

*   `build.gradle`: 프로젝트 의존성 및 빌드 설정
*   `Dockerfile`: 애플리케이션 배포를 위한 Docker 설정
*   `src/main/java/.../SampleController.java`: 웹 요청을 처리하는 메인 컨트롤러
*   `src/main/resources/application.properties`: 서버 포트 및 로그 설정
*   `src/main/webapp/WEB-INF/jsp/`: 웹 페이지(JSP) 파일

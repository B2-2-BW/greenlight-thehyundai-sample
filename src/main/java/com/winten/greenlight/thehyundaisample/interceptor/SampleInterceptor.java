package com.winten.greenlight.thehyundaisample.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SampleInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;
    private static final String QUEUE_API_BASE_URL = "http://localhost:8080/api/v1/queue";

    public SampleInterceptor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        System.out.println("[Interceptor] 요청 URL: " + requestUri + ", query: " + queryString);

        // 정적 리소스 및 대기/비활성화 페이지는 인터셉터 처리 제외
        if (requestUri.startsWith("/css/") || requestUri.startsWith("/js/") || requestUri.startsWith("/images/") ||
            requestUri.equals("/waiting") || requestUri.equals("/disabled")) {
            return true;
        }

        Long actionId = 10001L;

        // 요청 쿠키에서 X-GREENLIGHT-TOKEN 추출
        String greenlightToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-GREENLIGHT-TOKEN".equals(cookie.getName())) {
                    greenlightToken = cookie.getValue();
                    break;
                }
            }
        }

        // Core-API의 대기열 진입/확인 API URL 생성
        String checkOrEnterUrl = QUEUE_API_BASE_URL + "/check-or-enter?actionId=" + actionId;
        if (queryString != null && !queryString.isEmpty()) {
            checkOrEnterUrl += "&" + queryString;
        }

        // Core-API 호출을 위한 헤더 설정 (기존 토큰이 있다면 포함)
        HttpHeaders headers = new HttpHeaders();
        if (greenlightToken != null) {
            headers.set("X-GREENLIGHT-TOKEN", greenlightToken);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Core-API 호출
            ResponseEntity<Map> queueApiResponse = restTemplate.exchange(
                    checkOrEnterUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = queueApiResponse.getBody();
            if (responseBody != null) {
                String waitStatus = (String) responseBody.get("waitStatus");
                String newToken = (String) responseBody.get("token");

                // Core-API에서 받은 새 토큰을 쿠키에 저장
                if (newToken != null) {
                    Cookie tokenCookie = new Cookie("X-GREENLIGHT-TOKEN", newToken);
                    tokenCookie.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
                    response.addCookie(tokenCookie);
                }

                // 대기 상태에 따른 처리
                if ("WAITING".equals(waitStatus)) {
                    // WAITING 상태일 경우, 원래 요청 URL을 redirectUrl 파라미터로 waiting 페이지로 리다이렉트
                    String originalUrl = requestUri + (queryString != null ? "?" + queryString : "");
                    String redirectUrl = "/waiting?redirectUrl=" + URLEncoder.encode(originalUrl, StandardCharsets.UTF_8.toString());
                    response.sendRedirect(redirectUrl);
                    return false; // 요청 처리 중단
                } else if ("DISABLED".equals(waitStatus)) {
                    // 비활성화 상태일 경우, disabled 페이지로 리다이렉트
                    response.sendRedirect("/disabled");
                    return false; // 요청 처리 중단
                } else if ("READY".equals(waitStatus) || "BYPASSED".equals(waitStatus)) {
                    // READY 또는 BYPASSED 상태일 경우, 정상적으로 요청 처리 진행
                    return true; // 요청 처리 계속
                }
            }
        } catch (Exception e) {
            // Core-API 호출 중 오류 발생 시, 요청을 계속 진행 (오류 페이지 등으로 리다이렉트 고려 가능)
            System.err.println("대기열 API 호출 중 오류 발생: " + e.getMessage());
            return true;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

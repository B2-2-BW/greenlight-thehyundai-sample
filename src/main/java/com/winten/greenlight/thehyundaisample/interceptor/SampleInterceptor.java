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

        if (requestUri.startsWith("/css/") || requestUri.startsWith("/js/") || requestUri.startsWith("/images/") ||
            requestUri.equals("/waiting") || requestUri.equals("/disabled")) {
            return true;
        }

        Long actionId = 10001L;

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

        String checkOrEnterUrl = QUEUE_API_BASE_URL + "/check-or-enter?actionId=" + actionId;
        if (queryString != null && !queryString.isEmpty()) {
            checkOrEnterUrl += "&" + queryString;
        }

        HttpHeaders headers = new HttpHeaders();
        if (greenlightToken != null) {
            headers.set("X-GREENLIGHT-TOKEN", greenlightToken);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
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

                if (newToken != null) {
                    Cookie tokenCookie = new Cookie("X-GREENLIGHT-TOKEN", newToken);
                    tokenCookie.setPath("/");
                    response.addCookie(tokenCookie);
                }

                if ("WAITING".equals(waitStatus)) {
                    String originalUrl = requestUri + (queryString != null ? "?" + queryString : "");
                    String redirectUrl = "/waiting?redirectUrl=" + URLEncoder.encode(originalUrl, StandardCharsets.UTF_8.toString());
                    response.sendRedirect(redirectUrl);
                    return false;
                } else if ("DISABLED".equals(waitStatus)) {
                    response.sendRedirect("/disabled");
                    return false;
                } else if ("READY".equals(waitStatus) || "BYPASSED".equals(waitStatus)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("대기열 API 호출 중 오류 발생: " + e.getMessage());
            return true;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
package com.winten.greenlight.thehyundaisample.interceptor;

import com.winten.greenlight.thehyundaisample.WaitStatus;
import com.winten.greenlight.thehyundaisample.controller.EntryResponse;
import com.winten.greenlight.thehyundaisample.service.CachedActionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
public class SampleInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;
    private final CachedActionService cachedActionService;
    private static final String QUEUE_API_BASE_URL = "http://localhost:8080/api/v1/queue"; // 실제 Core API 주소

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();

        if (isExcluded(requestUri)) {
            return true;
        }

        String actionId = request.getParameter("actionId");

        // 1. 캐시된 Action 리스트를 통해 actionId의 유효성을 먼저 검사합니다.
        if (!cachedActionService.isValidAction(actionId)) {
            System.out.println("[Interceptor] 유효하지 않은 actionId(" + actionId + ")이므로 대기열 검사를 건너뜁니다.");
            return true; // 유효하지 않으면 API 호출 없이 바로 통과
        }

        System.out.println("[Interceptor] 유효한 actionId(" + actionId + ") 확인. Core API 대기열 검사를 시작합니다.");

        String greenlightToken = getTokenFromCookie(request);
        String checkOrEnterUrl = QUEUE_API_BASE_URL + "/check-or-enter";

        HttpHeaders headers = new HttpHeaders();
        if (greenlightToken != null) {
            headers.set("X-GREENLIGHT-TOKEN", greenlightToken);
        }

        // userId는 세션 등에서 가져오는 로직이 필요하나, 여기서는 임의의 값을 사용합니다.
        String userId = request.getSession().getId();
        Map<String, Object> requestBody = Map.of("actionId", actionId, "userId", userId);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 2. 유효성이 확인된 경우에만 실제 Core API를 호출합니다.
            ResponseEntity<EntryResponse> queueApiResponse = restTemplate.exchange(
                    checkOrEnterUrl,
                    HttpMethod.POST,
                    entity,
                    EntryResponse.class
            );

            EntryResponse responseBody = queueApiResponse.getBody();
            if (responseBody != null) {
                WaitStatus waitStatus = WaitStatus.valueOf(responseBody.getWaitStatus());
                String newToken = responseBody.getToken();

                if (newToken != null) {
                    Cookie tokenCookie = new Cookie("X-GREENLIGHT-TOKEN", newToken);
                    tokenCookie.setPath("/");
                    response.addCookie(tokenCookie);
                }

                if (WaitStatus.WAITING.equals(waitStatus)) {
                    String originalUrl = requestUri + (queryString != null ? "?" + queryString : "");
                    String redirectUrl = "/waiting?redirectUrl=" + URLEncoder.encode(originalUrl, StandardCharsets.UTF_8.toString());
                    response.sendRedirect(redirectUrl);
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("대기열 API 호출 중 오류 발생: " + e.getMessage());
            // API 장애 시에는 서비스를 계속 이용할 수 있도록 통과시키는 것이 일반적입니다.
            return true;
        }

        return true;
    }

    private boolean isExcluded(String requestUri) {
        return requestUri.startsWith("/css/") ||
               requestUri.startsWith("/js/") ||
               requestUri.startsWith("/images/") ||
               requestUri.equals("/waiting") ||
               requestUri.equals("/disabled") ||
               requestUri.equals("/favicon.ico");
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-GREENLIGHT-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

package com.winten.greenlight.thehyundaisample.interceptor;

import com.winten.greenlight.thehyundaisample.WaitStatus;
import com.winten.greenlight.thehyundaisample.controller.EntryResponse;
import com.winten.greenlight.thehyundaisample.service.CachedActionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class SampleInterceptor implements HandlerInterceptor {

    private final CachedActionService cachedActionService;

    public SampleInterceptor(CachedActionService cachedActionService) {
        this.cachedActionService = cachedActionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        System.out.println("[Interceptor] 요청 URL: " + requestUri + ", query: " + queryString);

        // 정적 리소스 및 시스템 페이지는 인터셉터 처리 제외
        if (isExcluded(requestUri)) {
            return true;
        }

        // 쿼리 파라미터에서 actionId 추출
        String actionId = request.getParameter("actionId");

        // actionId가 유효하지 않으면 대기열 로직을 건너뛰고 바로 진행
        if (!cachedActionService.isValidAction(actionId)) {
            System.out.println("[Interceptor] 유효하지 않은 actionId(" + actionId + ")이므로 대기열 검사를 건너뜁니다.");
            return true;
        }

        // 쿠키에서 토큰 추출
        String greenlightToken = getTokenFromCookie(request);

        // Core-API의 대기열 진입/확인 로직 (시뮬레이션)
        // 실제로는 CachedActionService 등을 통해 Core API를 호출해야 합니다.
        // EntryResponse entryResponse = cachedActionService.checkOrEnter(actionId, "someUserId", greenlightToken);
        EntryResponse entryResponse = new EntryResponse("DUMMY_STATUS", "DUMMY_TOKEN", 1L); // 임시 응답

        WaitStatus waitStatus = WaitStatus.valueOf(entryResponse.getWaitStatus());
        String newToken = entryResponse.getToken();

        // 새 토큰을 쿠키에 저장
        if (newToken != null) {
            Cookie tokenCookie = new Cookie("X-GREENLIGHT-TOKEN", newToken);
            tokenCookie.setPath("/");
            response.addCookie(tokenCookie);
        }

        // 대기 상태에 따른 처리
        if (WaitStatus.WAITING.equals(waitStatus)) {
            String originalUrl = requestUri + (queryString != null ? "?" + queryString : "");
            String redirectUrl = "/waiting?redirectUrl=" + URLEncoder.encode(originalUrl, StandardCharsets.UTF_8.toString());
            response.sendRedirect(redirectUrl);
            return false;
        }

        // READY, BYPASSED, DISABLED 상태는 정상 진행
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
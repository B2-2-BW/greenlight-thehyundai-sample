package com.winten.greenlight.thehyundaisample.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class SampleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Interceptor 로직 수행
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        System.out.println("[Interceptor] 요청 URL: " + requestUri + ", query: " + queryString);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
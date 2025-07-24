package com.winten.greenlight.thehyundaisample.config;

import com.winten.greenlight.thehyundaisample.interceptor.SampleInterceptor;
import com.winten.greenlight.thehyundaisample.service.CachedActionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CachedActionService cachedActionService;

    public WebConfig(CachedActionService cachedActionService) {
        this.cachedActionService = cachedActionService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SampleInterceptor(cachedActionService))
                .addPathPatterns("/**"); // 모든 URL에 대해 인터셉터 적용
    }
}

package com.winten.greenlight.thehyundaisample.config;

import com.winten.greenlight.thehyundaisample.interceptor.SampleInterceptor;
import com.winten.greenlight.thehyundaisample.service.CachedActionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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
        // SampleInterceptor가 RestTemplate과 CachedActionService를 모두 사용하도록 주입합니다.
        registry.addInterceptor(sampleInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SampleInterceptor sampleInterceptor() {
        // 인터셉터 Bean 생성 시, 필요한 의존성을 직접 주입합니다.
        return new SampleInterceptor(restTemplate(), cachedActionService);
    }
}
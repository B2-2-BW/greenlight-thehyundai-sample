package com.winten.greenlight.thehyundaisample.config;

import com.winten.greenlight.thehyundaisample.interceptor.SampleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final SampleInterceptor sampleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sampleInterceptor).addPathPatterns("/**")
                .addPathPatterns("/**");
    }
}
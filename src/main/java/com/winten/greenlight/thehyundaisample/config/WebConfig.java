package com.winten.greenlight.thehyundaisample.config;

import com.winten.greenlight.thehyundaisample.interceptor.SampleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SampleInterceptor()).addPathPatterns("/**")
                .addPathPatterns("/**");
    }
}
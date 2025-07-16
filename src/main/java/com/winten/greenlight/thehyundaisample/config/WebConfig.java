package com.winten.greenlight.thehyundaisample.config;

import com.winten.greenlight.thehyundaisample.interceptor.SampleInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // SampleInterceptor에 RestTemplate을 주입하기 위해 Bean으로 등록
        registry.addInterceptor(sampleInterceptor()).addPathPatterns("/**");
    }

    // RestTemplate Bean 추가
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // SampleInterceptor Bean 추가 (RestTemplate 주입을 위해)
    @Bean
    public SampleInterceptor sampleInterceptor() {
        return new SampleInterceptor(restTemplate());
    }
}
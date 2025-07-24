package com.winten.greenlight.thehyundaisample.config;

import com.winten.greenlight.thehyundaisample.interceptor.SampleInterceptor;
import com.winten.greenlight.thehyundaisample.service.CachedActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CachedActionService cachedActionService;
    private final SampleInterceptor sampleInterceptor; // SampleInterceptor를 주입받도록 추가

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //Spring이 관리하는 SampleInterceptor 빈을 직접 주입받아 사용합니다.
        // 직접 주입받은 sampleInterceptor 사용
        registry.addInterceptor(sampleInterceptor).addPathPatterns("/**");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public SampleInterceptor sampleInterceptor() {
//        // 인터셉터 Bean 생성 시, 필요한 의존성을 직접 주입합니다.
//        return new SampleInterceptor(restTemplate(), cachedActionService);
//    }
}

package com.winten.greenlight.thehyundaisample.greenlight;

import lombok.Getter;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
public class GreenlightRestTemplateProvider {
    private final RestTemplate restTemplate;

    public GreenlightRestTemplateProvider() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(factory);
    }
}
package com.winten.greenlight.thehyundaisample.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class CachedActionService {

    private final RestTemplate restTemplate;
    @Value("${greenlight.core.api.action.list.url}")
    private String coreApiActionListUrl;
    @Value("${greenlight.api.key}")
    private String apiKey;

    public CachedActionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Core API를 호출하여 Action 리스트를 가져옵니다.
     * @Cacheable("actions")에 의해 이 메소드의 결과는 "actions" 캐시에 저장되며,
     * 최초 호출 이후에는 캐시된 값을 반환합니다.
     * @return ActionDto 리스트
     */
    @Cacheable("actions")
    public List<ActionDto> getActions() {
        System.out.println("===== 어드민 호출: Action 리스트를 가져옵니다. =====");
        try {
            HttpHeaders headers = new HttpHeaders();
            System.out.println("API Key: " + apiKey);
            headers.set("X-GREENLIGHT-API-KEY", apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<ActionDto>> response = restTemplate.exchange(
                    coreApiActionListUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ActionDto>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Core API에서 Action 리스트를 가져오는 중 오류 발생: " + e.getMessage());
            // 오류 발생 시 빈 리스트 반환 또는 예외 처리 정책에 따름
            return Collections.emptyList();
        }
    }

    /**
     * 캐시된 Action 리스트에서 actionId가 유효한지 확인합니다.
     * @param actionId 확인할 actionId
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidAction(String actionId) {
        if (actionId == null || actionId.trim().isEmpty()) {
            return false;
        }
        List<ActionDto> actionGroups = getActions(); // 이 호출은 캐시된 데이터를 사용합니다.
        return actionGroups.stream()
                .flatMap(group -> group.getActions().stream())
                .anyMatch(action -> actionId.equals(action.getId().toString()));
    }
}

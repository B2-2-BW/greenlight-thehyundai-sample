package com.winten.greenlight.thehyundaisample.greenlight;

import com.winten.greenlight.thehyundaisample.greenlight.dto.Action;
import com.winten.greenlight.thehyundaisample.greenlight.dto.ActionRequest;
import com.winten.greenlight.thehyundaisample.greenlight.dto.EntryTicket;
import com.winten.greenlight.thehyundaisample.greenlight.dto.TicketVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GreenlightCoreApiClient {
    @Value("${greenlight.api.core.url}")
    private String baseUrl;

    @Value("${greenlight.api.core.key}")
    private String apikey;

    private final GreenlightRestTemplateProvider restTemplateProvider;

    public boolean fetchHealth() {
        String url = baseUrl + "/health";
        ResponseEntity<String> response = restTemplateProvider.getRestTemplate().getForEntity(url, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    public List<Action> fetchActions() {
        // 티켓이 없으면, 또는 현재 actionId와 일치하지 않으면?
        String url = baseUrl + "/actions";
        HttpHeaders headers = new HttpHeaders();
        headers.set(GreenlightConstant.GREENLIGHT_API_KEY, apikey);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
        ResponseEntity<Action[]> response = restTemplateProvider.getRestTemplate().exchange(url, HttpMethod.GET, entity, Action[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return new ArrayList<>();
        }
    }

    public EntryTicket issueEntryTicket(Action action, String currentUrl) {
        assert action != null;

        String url = baseUrl + "/api/v1/queue/check-or-enter";

        HttpEntity<ActionRequest> entity = new HttpEntity<>(
                ActionRequest.builder()
                        .actionId(action.getId())
                        .destinationUrl(currentUrl)
                        .build()
        );

        ResponseEntity<EntryTicket> response = restTemplateProvider.getRestTemplate().exchange(url, HttpMethod.POST, entity, EntryTicket.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public TicketVerification verifyTicket(String ticket) {
        String url = baseUrl + "/api/v1/customer/verify";
        HttpHeaders headers = new HttpHeaders();
        headers.set(GreenlightConstant.GREENLIGHT_TOKEN, ticket);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

        ResponseEntity<TicketVerification> response = restTemplateProvider.getRestTemplate().exchange(url, HttpMethod.POST, entity, TicketVerification.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            return null;
        }
    }
}
package com.winten.greenlight.thehyundaisample.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CachedActionService {

    /**
     * Core API를 호출하여 Action 리스트를 가져오는 것을 시뮬레이션합니다.
     * @Cacheable("actions")에 의해 이 메소드의 결과는 "actions" 캐시에 저장되며,
     * 최초 호출 이후에는 캐시된 값을 반환합니다.
     * @return ActionDto 리스트
     */
    @Cacheable("actions")
    public List<ActionDto> getActions() {
        System.out.println("===== Core API 호출 시뮬레이션: Action 리스트를 가져옵니다. =====");
        // 실제로는 WebClient 등을 사용하여 Core API를 호출해야 합니다.
        return Arrays.asList(
                new ActionDto("event-001", "더현대 서울 팝업스토어", "/popup/seoul"),
                new ActionDto("event-002", "더현대 대구 전시회", "/exhibition/daegu"),
                new ActionDto("event-003", "판교 현대백화점 명품관", "/luxury/pangyo")
        );
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
        List<ActionDto> actions = getActions(); // 이 호출은 캐시된 데이터를 사용합니다.
        return actions.stream().anyMatch(action -> actionId.equals(action.getActionId()));
    }

    /**
     * actionId로 ActionDto를 찾습니다.
     * @param actionId 찾을 actionId
     * @return Optional<ActionDto>
     */
    public Optional<ActionDto> getActionById(String actionId) {
        if (actionId == null || actionId.trim().isEmpty()) {
            return Optional.empty();
        }
        return getActions().stream()
                .filter(action -> actionId.equals(action.getActionId()))
                .findFirst();
    }
}

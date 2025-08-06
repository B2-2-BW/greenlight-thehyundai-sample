package com.winten.greenlight.thehyundaisample.greenlight;

import com.winten.greenlight.thehyundaisample.greenlight.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreenlightService {

    private final GreenlightCoreApiClient greenlightCoreApiClient;
    private final JwtUtil jwtUtil;
    /**
     * 대기열 상태를 확인하고 다음 행동을 결정합니다.
     * 인터셉터에서 이 메서드를 호출합니다.
     */
    public QueueResult checkQueue(HttpServletRequest request) {
        // TODO 테스트 필요

        // 1. 대기열이 꺼져있다면 진행
        if (!GreenlightContext.isEnabled()) {
            log.info("대기열 꺼져있음, proceed");
            return QueueResult.proceed();
        }

        // 2. 이 URL에 해당하는 action이 없다면 진행
        StringBuffer url = request.getRequestURL();
        Action action = GreenlightContext.getActionFromUrl(url.toString());
        if (action == null) {
            log.info("해당하는 action 없음, proceed");
            return QueueResult.proceed();
        }

        // 3. 대기열 적용 대상이 아니면 즉시 통과
        if (!isGreenlightActionApplicable(action, request.getParameterMap())) {
            log.info("대기열 적용 조건이 아님, proceed");
            return QueueResult.proceed(); // "대기열 미적용. 자유입장"
        }

        // 4. cookie에서 토큰 및 Full URL 추출
        String greenlightToken = extractGreenlightTokenFromRequest(request); // result is nullable
        String fullRequestUrl = getFullUrlFromRequest(request);

        // 5. 토큰이 유효하고 사용 가능한 경우 즉시 통과
        if (isTokenValidAndVerified(greenlightToken, fullRequestUrl, action)) {
            log.info("대기 완료했음, proceed");
            return QueueResult.proceed(greenlightToken); // "가려던 화면으로 바로 이동. (정상진입)"
        }

        // 위의 조건들을 통과하지 못했다면, 토큰이 없거나, 만료되었거나, 현재 액션과 맞지 않는 경우.
        // 따라서 신규 티켓 발급 로직을 수행합니다.
        return issueNewTicketAndDecideAction(action, fullRequestUrl);
    }

    private String extractGreenlightTokenFromRequest(HttpServletRequest request) {
        String greenlightToken = null;
        for (Cookie cookie: request.getCookies()) {
            if (GreenlightHeader.GREENLIGHT_TOKEN.equals(cookie.getName())) {
                greenlightToken = cookie.getValue();
            };
        }
        return greenlightToken;
    }

    private String getFullUrlFromRequest(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append('?').append(queryString);
        }
        return requestURL.toString();
    }
    /**
     * 주어진 토큰이 현재 액션에 유효하고 사용 가능한지 검증하는 로직
     */
    private boolean isTokenValidAndVerified(String greenlightToken, String fullRequestUrl, Action action) {
        if (greenlightToken == null || greenlightToken.isBlank()) {
            return false;
        }

        try {
            Customer customer = jwtUtil.getCustomerFromToken(greenlightToken);

            // 토큰 내용이 부적절하거나, 현재 액션 그룹과 맞지 않으면 유효하지 않음
            if (customer.getActionId() == null || customer.getCustomerId() == null) {
                return false; // "정상적이지 않은 Customer"
            }
            if (!Objects.equals(action.getActionGroupId(), customer.getActionGroupId()) && action.getActionType() == ActionType.DIRECT) {
                return false; // 다른 액션 그룹 토큰
            }

            // 최종적으로 대기열 서비스에 토큰 검증 요청
            var ticketVerification = greenlightCoreApiClient.verifyTicket(greenlightToken);
            return ticketVerification.getVerified();

        } catch (Exception e) {
            // JWT 파싱 오류 등 예외 발생 시 유효하지 않은 토큰으로 간주
            // log.error("Token validation failed", e);
            return false;
        }
    }

    /**
     * 신규 티켓을 발급받고, 그 결과에 따라 다음 행동을 결정하는 로직 (중복 제거)
     */
    private QueueResult issueNewTicketAndDecideAction(Action action, String currentUrl) {
        EntryTicket entryTicket = greenlightCoreApiClient.issueEntryTicket(action, currentUrl);

        if (entryTicket.getWaitStatus() == WaitStatus.WAITING) {
            log.info("대기해야함, issueAndWait");
            // "대기열 화면으로 redirect"
            return QueueResult.issueTicketAndWait(entryTicket.getJwtToken());
        }

        if (entryTicket.getWaitStatus() == WaitStatus.READY) {
            // 바로 입장 가능한 티켓을 받음 -> 즉시 사용 처리 후 통과
            // 이 경우, 새로 발급된 토큰을 클라이언트에게 전달해야 함
            var verification = greenlightCoreApiClient.verifyTicket(entryTicket.getJwtToken());
            if (verification.getVerified()) {
                // "가려던 화면으로 바로 이동 (verifyTicket으로 바로 사용처리)"
                log.info("바로 입장 가능, proceed");
                return QueueResult.proceed(entryTicket.getJwtToken());
            } else {
                // 바로 입장 티켓을 받았는데 검증에 실패한 이례적인 상황
                // 대기열로 보내는 것이 안전
                log.info("검증 실패 원인 알 수 없지만 대기해야함, issueAndWait");
                return QueueResult.issueTicketAndWait(entryTicket.getJwtToken());
            }
        }

        // 예외적인 WaitStatus 처리 (기본적으로는 대기)
        log.info("홈으로, issueAndWait");
        return QueueResult.gotoHome();
    }

    private boolean isGreenlightActionApplicable(Action action, Map<String, String[]> requestParams) {
        DefaultRuleType defaultRule = action.getDefaultRuleType();
        if (defaultRule == DefaultRuleType.ALL) {
            return true;
        }

        if (defaultRule == DefaultRuleType.INCLUDE && requestParams != null) {
            for (ActionRule rule : action.getActionRules()) {
                for (String requestValue : requestParams.get(rule.getParamName())) {
                    if (requestValue != null && this.matches(requestValue, rule.getParamValue(), rule.getMatchOperator())) {
                        return true; // 규칙 일치 -> 대기열 적용
                    }
                }
            }
            return false; // 어떤 규칙에도 일치하지 않음 -> 대기열 미적용
        } else if (defaultRule == DefaultRuleType.EXCLUDE) { // DefaultRuleType.EXCLUDE: 규칙 중 하나라도 일치할 경우 대기열을 적용하지 않습니다.
            for (ActionRule rule : action.getActionRules()) {
                for (String requestValue : requestParams.get(rule.getParamName())) {
                    if (requestValue != null && this.matches(requestValue, rule.getParamValue(), rule.getMatchOperator())) {
                        return false; // 규칙 일치 -> 대기열 미적용
                    }
                }
            }
            return true; // 어떤 규칙에도 일치하지 않음 -> 대기열 적용
        }
        return true; // 안전을 위해, 정의되지 않은 정책은 모두 대기열 적용
    }

    private boolean matches(String requestValue, String ruleValue, MatchOperator operator) {
        if (requestValue == null || ruleValue == null || operator == null) {
            return false;
        }
        switch (operator) {
            case EQUAL:
                return requestValue.equals(ruleValue);
            case CONTAINS:
                return requestValue.contains(ruleValue);
            case STARTSWITH:
                return requestValue.startsWith(ruleValue);
            case ENDSWITH:
                return requestValue.endsWith(ruleValue);
            default:
                return false;
        }
    }
}
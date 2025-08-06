package com.winten.greenlight.thehyundaisample.interceptor;

import com.winten.greenlight.thehyundaisample.greenlight.GreenlightHeader;
import com.winten.greenlight.thehyundaisample.greenlight.GreenlightService;
import com.winten.greenlight.thehyundaisample.greenlight.dto.NextBehavior;
import com.winten.greenlight.thehyundaisample.greenlight.dto.QueueResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class SampleInterceptor implements HandlerInterceptor {

    private final GreenlightService greenlightService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 대기열 검증 로직 호출
        boolean checkResult = checkGreenlight(request, response);
        if (!checkResult) {
            return false;
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private boolean checkGreenlight(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueueResult result = greenlightService.checkQueue(request);

        if (result.getNextBehavior() == NextBehavior.ISSUE_TICKET_AND_WAIT) {
            setTokenCookie(response, result.getNewGreenlightToken());
            response.sendRedirect("/waitingTest");
            // 이 경우에만 인터셉터 체인을 중단하고 false를 반환합니다.
            return false;
        }

        if (result.getNextBehavior() == NextBehavior.GOTO_HOME) {
            // 잘못된 접근으로 오류 또는 홈으로
            response.sendRedirect("/error");
            return false;
        }

        // 3. 결과에 따른 분기 처리
        if (result.getNextBehavior() == NextBehavior.PROCEED) {
            if (result.getNewGreenlightToken() != null) {
                setTokenCookie(response, result.getNewGreenlightToken());
            }
        }
        return true;
    }

    private void setTokenCookie(HttpServletResponse response, String newGreenlightToken) {
        response.addCookie(new Cookie(GreenlightHeader.GREENLIGHT_TOKEN, newGreenlightToken));
    }

}
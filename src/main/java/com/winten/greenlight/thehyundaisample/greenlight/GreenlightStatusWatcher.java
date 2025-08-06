package com.winten.greenlight.thehyundaisample.greenlight;

import com.winten.greenlight.thehyundaisample.greenlight.dto.Action;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GreenlightStatusWatcher {
    private final GreenlightCoreApiClient greenlightCoreApiClient;
    private int errorCount = 0;

    @Scheduled(fixedDelay = 10000L)
    public void run() {
        try {
            updateStatus();
        }
        catch (Exception e) { // 오류 발생 시
            if (errorCount == 0) {
                log.error("Greenlight 시스템 이상 발생.", e);
            }
            errorCount++;
            GreenlightContext.setEnabled(false);
            GreenlightContext.clearActions();
        }
    }

    private void updateStatus() {
        boolean isHealthy = greenlightCoreApiClient.fetchHealth();
        GreenlightContext.setEnabled(isHealthy);

        if (isHealthy) {
            List<Action> actions = greenlightCoreApiClient.fetchActions();
            GreenlightContext.updateActions(actions);
        } else {
            GreenlightContext.clearActions();
        }
    }

}
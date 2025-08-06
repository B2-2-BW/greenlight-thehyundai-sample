package com.winten.greenlight.thehyundaisample.greenlight.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class QueueResult {
    private final NextBehavior nextBehavior;
    private final String newGreenlightToken;

    private QueueResult(NextBehavior nextBehavior, String newGreenlightToken) {
        this.nextBehavior = nextBehavior;
        this.newGreenlightToken = newGreenlightToken;
    }

    // 정적 팩토리 메서드
    public static QueueResult proceed() {
        return QueueResult.proceed(null);
    }

    public static QueueResult proceed(String newGreenlightToken) {
        return new QueueResult(NextBehavior.PROCEED, newGreenlightToken);
    }

    public static QueueResult issueTicketAndWait(String newGreenlightToken) {
        return new QueueResult(NextBehavior.ISSUE_TICKET_AND_WAIT, newGreenlightToken);
    }

    public static QueueResult gotoHome() {
        return new QueueResult(NextBehavior.GOTO_HOME, null);
    }
}
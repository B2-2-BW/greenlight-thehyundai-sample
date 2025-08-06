package com.winten.greenlight.thehyundaisample.greenlight.dto;

public enum NextBehavior {
    /** 자유 입장 (대기열 비적용) */
    PROCEED,
    /** 대기열 화면으로 이동 필요 */
    ISSUE_TICKET_AND_WAIT,
    /** 잘못된 접근, 홈으로 */
    GOTO_HOME
}
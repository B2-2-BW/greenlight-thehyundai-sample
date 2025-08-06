package com.winten.greenlight.thehyundaisample.greenlight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private Long actionGroupId;
    private Long actionId;
    private String customerId;
    private Long score;
    private WaitStatus waitStatus;
    private String destinationUrl;
    private Long waitTimeMs;
    private String forwardedFrom;
}
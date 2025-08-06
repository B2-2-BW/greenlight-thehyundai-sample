package com.winten.greenlight.thehyundaisample.greenlight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketVerification {
    private Long actionId;
    private Long actionGroupId;
    private String customerId;
    private Boolean verified;
    private String reason;
}
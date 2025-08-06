package com.winten.greenlight.thehyundaisample.greenlight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionRequest {
    private Long actionId;
    private String destinationUrl;
}
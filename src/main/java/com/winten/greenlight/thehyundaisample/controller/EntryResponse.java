package com.winten.greenlight.thehyundaisample.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryResponse {
    private String waitStatus;
    private String jwt;
    private Long rank;
}

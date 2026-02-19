package com.fraudclaimdetector.claim_fraud_ai_service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GeminiRequest(List<Content> contents) {
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    public static GeminiRequest fromPrompt(String prompt) {
        return GeminiRequest.builder()
                .contents(List.of(new Content(List.of(new Part(prompt)))))
                .build();
    }
}
package com.fraudclaimdetector.claim_fraud_ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAnalysisResponse {

    private String claimId;
    private String userId;
    private String originalFileName;
    private String summary;
    private List<String> fraudIndicators;
    private List<String> redFlags;
    private List<String> recommendations;
    private LocalDateTime analyzedAt;
    private String status;
    private String errorMessage;
}
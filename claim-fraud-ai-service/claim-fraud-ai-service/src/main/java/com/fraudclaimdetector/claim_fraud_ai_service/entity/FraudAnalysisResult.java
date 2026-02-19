package com.fraudclaimdetector.claim_fraud_ai_service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "claim_fraud_analyses")
public class FraudAnalysisResult {

    @Id
    private String id;

    private String claimId;
    private String userId;
    private String claimType;
    private String originalFileName;
    private String summary;
    private String fraudIndicators;       // JSON array string e.g. ["Suspicious billing patterns", ...]
    private String redFlags;              // JSON array string
    private String recommendations;       // JSON array string
    private Double fraudProbability;      // 0.00 – 1.00
    private LocalDateTime analyzedAt;
    private String status;                // COMPLETED, FAILED, PENDING
    private String errorMessage;
}
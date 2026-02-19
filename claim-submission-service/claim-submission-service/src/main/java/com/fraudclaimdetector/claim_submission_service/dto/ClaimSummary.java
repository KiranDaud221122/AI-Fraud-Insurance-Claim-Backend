package com.fraudclaimdetector.claim_submission_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSummary {

    private String claimId;
    private String originalFileName;
    private long fileSizeBytes;
    private LocalDateTime submissionDate;
    private String status;
}
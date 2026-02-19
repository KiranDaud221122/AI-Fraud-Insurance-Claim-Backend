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
public class UploadResponse {

    private String claimId;
    private String originalFileName;
    private String contentType;
    private long fileSizeBytes;
    private String status;
    private LocalDateTime submissionDate;
    private String message;
}
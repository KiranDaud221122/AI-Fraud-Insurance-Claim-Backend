package com.fraudclaimdetector.claim_fraud_ai_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSubmittedEvent implements Serializable {

    private String claimId;
    private String userId;
    private String gridFsFileId;
    private String originalFileName;
    private String contentType;
    private long fileSizeBytes;
    private String claimType;  // ← NEW FIELD: MEDICAL / VEHICLE / LIFE / OTHER
}
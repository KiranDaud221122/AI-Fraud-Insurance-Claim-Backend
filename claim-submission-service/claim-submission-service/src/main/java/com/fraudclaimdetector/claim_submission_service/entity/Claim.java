package com.fraudclaimdetector.claim_submission_service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "claims")
public class Claim {

    @Id
    private String id;
    private String claimId;
    private String userId;
    private String originalFileName;
    private String contentType;
    private long fileSizeBytes;
    private LocalDateTime submissionDate;
    private String status;
    private String gridFsFileId;
}

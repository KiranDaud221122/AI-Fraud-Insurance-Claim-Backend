package com.fraudclaimdetector.claim_submission_service.service;

import com.fraudclaimdetector.claim_submission_service.entity.Claim;
import com.fraudclaimdetector.claim_submission_service.event.ClaimSubmittedEvent;
import com.fraudclaimdetector.claim_submission_service.repository.ClaimRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {

    private final GridFsOperations gridFsOperations;
    private final ClaimRepository claimRepository;
    private final KafkaProducerService kafkaProducerService;

    public String submitClaim(MultipartFile file, String userId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required and cannot be empty");
        }

        log.debug("Starting upload | file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());

        // 1. Store file in GridFS
        ObjectId gridFsId = gridFsOperations.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
        String gridFsFileId = gridFsId.toHexString();
        log.info("File stored in GridFS | id: {}", gridFsFileId);

        // 2. Create and save metadata
        Claim claim = new Claim();
        claim.setClaimId(UUID.randomUUID().toString());
        claim.setUserId(userId);
        claim.setOriginalFileName(file.getOriginalFilename());
        claim.setContentType(file.getContentType());
        claim.setFileSizeBytes(file.getSize());
        claim.setSubmissionDate(LocalDateTime.now());
        claim.setStatus("SUBMITTED");
        claim.setGridFsFileId(gridFsFileId);

        log.info("Saving claim metadata | claimId: {}, userId: {}", claim.getClaimId(), claim.getUserId());

        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim saved successfully | id: {}, claimId: {}", savedClaim.getId(), savedClaim.getClaimId());

        // 3. Publish Kafka event
        try {
            ClaimSubmittedEvent event = new ClaimSubmittedEvent(
                    savedClaim.getClaimId(),
                    savedClaim.getUserId(),
                    savedClaim.getGridFsFileId(),
                    savedClaim.getOriginalFileName(),
                    savedClaim.getContentType(),
                    savedClaim.getFileSizeBytes()
            );
            kafkaProducerService.sendClaimSubmittedEvent(event);
            log.info("Kafka event published successfully | claimId: {}", savedClaim.getClaimId());
        } catch (Exception e) {
            log.error("Failed to publish Kafka event for claimId: {}", savedClaim.getClaimId(), e);
            // Optional: mark claim status as "EVENT_FAILED" if you want to retry later
        }

        return savedClaim.getClaimId();
    }

    public Optional<Claim> getClaimById(String claimId) {
        return claimRepository.findByClaimId(claimId);
    }

    public List<Claim> getClaimsByUser(String userId) {
        return claimRepository.findByUserId(userId);
    }

    public Resource getFileResource(String gridFsFileId) throws IOException {
        GridFSFile file = gridFsOperations.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(gridFsFileId)))
        );
        if (file == null) {
            throw new IOException("File not found in GridFS for id: " + gridFsFileId);
        }
        GridFsResource gridFsResource = gridFsOperations.getResource(file);
        return new InputStreamResource(gridFsResource.getInputStream());
    }
}
package com.fraudclaimdetector.claim_submission_service.repository;

import com.fraudclaimdetector.claim_submission_service.entity.Claim;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends MongoRepository<Claim, String> {

    Optional<Claim> findByClaimId(String claimId);
    List<Claim> findByUserId(String userId);
    List<Claim> findByUserIdAndStatus(String userId, String status);
}
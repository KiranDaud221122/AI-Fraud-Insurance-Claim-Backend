package com.fraudclaimdetector.claim_fraud_ai_service.repository;

import com.fraudclaimdetector.claim_fraud_ai_service.entity.FraudAnalysisResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FraudAnalysisRepository extends MongoRepository<FraudAnalysisResult, String> {
    List<FraudAnalysisResult> findByUserId(String userId);
    Optional<FraudAnalysisResult> findByClaimId(String claimId);
}
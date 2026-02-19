package com.fraudclaimdetector.claim_fraud_ai_service.service;

import com.fraudclaimdetector.claim_fraud_ai_service.entity.FraudAnalysisResult;
import com.fraudclaimdetector.claim_fraud_ai_service.event.ClaimSubmittedEvent;
import com.fraudclaimdetector.claim_fraud_ai_service.repository.FraudAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimUploadListener {

    private final FraudAnalysisAIService analysisService;
    private final FraudAnalysisRepository analysisRepository;

    @KafkaListener(topics = "${kafka.topic.name:contract-uploaded}", groupId = "ai-review-group")
    public void processClaim(ClaimSubmittedEvent event) {
        log.info("Received claim event | claimId: {}", event.getClaimId());

        FraudAnalysisResult result = analysisService.generateFraudAnalysis(event);
        analysisRepository.save(result);

        log.info("AI fraud analysis saved for claimId: {}", event.getClaimId());
    }
}
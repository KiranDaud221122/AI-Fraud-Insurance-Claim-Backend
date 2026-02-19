package com.fraudclaimdetector.claim_fraud_ai_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudclaimdetector.claim_fraud_ai_service.dto.FraudAnalysisResponse;
import com.fraudclaimdetector.claim_fraud_ai_service.entity.FraudAnalysisResult;
import com.fraudclaimdetector.claim_fraud_ai_service.repository.FraudAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fraud-analyses")
public class FraudAnalysisController {

    private final FraudAnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FraudAnalysisResult>> getUserAnalyses(@PathVariable String userId) {
        List<FraudAnalysisResult> analyses = analysisRepository.findByUserId(userId);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<FraudAnalysisResponse> getClaimAnalysis(@PathVariable String claimId) {
        return analysisRepository.findByClaimId(claimId)
                .map(analysis -> {
                    FraudAnalysisResponse response = FraudAnalysisResponse.builder()
                            .claimId(analysis.getClaimId())
                            .userId(analysis.getUserId())
                            .originalFileName(analysis.getOriginalFileName())
                            .summary(analysis.getSummary())
                            .fraudIndicators(parseJsonList(analysis.getFraudIndicators()))
                            .redFlags(parseJsonList(analysis.getRedFlags()))
                            .recommendations(parseJsonList(analysis.getRecommendations()))
                            .analyzedAt(analysis.getAnalyzedAt())
                            .status(analysis.getStatus())
                            .errorMessage(analysis.getErrorMessage())
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper to parse JSON string to List<String>
    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse JSON list: {}", json);
            return List.of(json); // fallback
        }
    }
}
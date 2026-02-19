package com.fraudclaimdetector.claim_fraud_ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudclaimdetector.claim_fraud_ai_service.entity.FraudAnalysisResult;
import com.fraudclaimdetector.claim_fraud_ai_service.event.ClaimSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAnalysisAIService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public FraudAnalysisResult generateFraudAnalysis(ClaimSubmittedEvent event) {
        log.info("Generating AI fraud analysis for claimId: {}", event.getClaimId());

        String prompt = createFraudPrompt(event);
        String aiResponse = geminiService.getAnswer(prompt);

        FraudAnalysisResult result = processAiResponse(event, aiResponse);
        result.setStatus("COMPLETED");
        result.setAnalyzedAt(LocalDateTime.now());

        log.info("Generated fraud analysis for claimId: {}", event.getClaimId());
        return result;
    }

    private String createFraudPrompt(ClaimSubmittedEvent event) {
        return String.format("""
                You are a professional insurance fraud investigator specializing in detecting fake or suspicious claims in medical, vehicle, and life insurance domains.

                Your task is to analyze the submitted claim details and determine the likelihood of fraud and human readable and dont add large messages kepp short and exact .

                Return **ONLY** valid JSON with no markdown, comments, explanations, or extra text outside the JSON object:

                {
                  "fraudProbabilityPercent": number between 0 and 100 (integer or float),
                  "classification": "REAL" or "FAKE" or "SUSPICIOUS" (based on the analysis),
                  "summary": "Professional one-paragraph summary of the claim and fraud risk assessment",
                  "fraudIndicators": ["clearly listed fraud signals or suspicious elements found in the claim"],
                  "redFlags": ["high-risk observations also add perentace of fraud probability for each red flag if possible"],
                  "recommendations": ["actionable next steps for the insurance" ]
                }

                Claim details to analyze:
                - Claim Type: %s
                - File name: %s
                - File size: %d bytes
                - Submitting user: %s
                - Claim ID: %s
                - Full claim document content: [extracted text would go here]

                Be objective, evidence-based, and professional.
                Respond **ONLY** with the JSON object.
                """,
                event.getClaimType() != null ? event.getClaimType() : "UNKNOWN",
                event.getOriginalFileName(),
                event.getFileSizeBytes(),
                event.getUserId(),
                event.getClaimId()
        );
    }

    private FraudAnalysisResult processAiResponse(ClaimSubmittedEvent event, String aiResponse) {
        FraudAnalysisResult r = new FraudAnalysisResult();
        r.setClaimId(event.getClaimId());
        r.setUserId(event.getUserId());
        r.setOriginalFileName(event.getOriginalFileName());
        r.setAnalyzedAt(LocalDateTime.now());
        r.setStatus("COMPLETED");

        try {
            // Step 1: Parse Gemini full response
            JsonNode root = objectMapper.readTree(aiResponse);

            // Step 2: Extract the actual text from candidates
            JsonNode textNode = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            if (textNode.isMissingNode()) {
                throw new RuntimeException("No text found in Gemini response");
            }

            String jsonText = textNode.asText().trim();
            jsonText = jsonText.replaceAll("(?s)^```json\\s*(.*?)\\s*```$", "$1").trim();

            JsonNode analysis = objectMapper.readTree(jsonText);

            // Set fields with safe defaults
            r.setSummary(analysis.path("summary").asText("No summary available"));
            r.setFraudIndicators(analysis.path("fraudIndicators").toString());
            r.setRedFlags(analysis.path("redFlags").toString());
            r.setRecommendations(analysis.path("recommendations").toString());

            // Optional: store fraud probability if you add it to entity
            // r.setFraudProbability(analysis.path("fraudProbabilityPercent").asDouble(0.0));

            // Print beautiful formatted report
            printBeautifulFraudReport(r, analysis);

        } catch (Exception e) {
            log.error("Failed to parse Gemini response for claimId: {}", event.getClaimId(), e);
            r.setSummary(aiResponse); // fallback: raw response
            r.setErrorMessage("Parsing failed: " + e.getMessage());

            System.out.println("AI Fraud Analysis Parsing Failed: " + e.getMessage());
        }

        return r;
    }

    private void printBeautifulFraudReport(FraudAnalysisResult r, JsonNode analysis) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     Fraud Analysis Report – Professional Summary     ");
        System.out.println("=".repeat(60));
        System.out.printf("Claim ID       : %s%n", r.getClaimId());
        System.out.printf("User           : %s%n", r.getUserId());
        System.out.printf("File Name      : %s%n", r.getOriginalFileName());
        System.out.printf("Analysis Status: %s%n", r.getStatus());
        System.out.printf("Analyzed At    : %s%n%n", r.getAnalyzedAt());

        // 1. Summary
        System.out.println("1. Executive Summary");
        System.out.println(analysis.path("summary").asText("No summary available"));
        System.out.println();

        // 2. Fraud Indicators
        System.out.println("2. Detected Fraud Indicators");
        JsonNode indicators = analysis.path("fraudIndicators");
        if (indicators.isArray() && !indicators.isEmpty()) {
            indicators.forEach(ind -> System.out.println("• " + ind.asText()));
        } else {
            System.out.println("• No specific fraud indicators identified");
        }
        System.out.println();

        // 3. Red Flags
        System.out.println("3. Critical Red Flags");
        JsonNode flags = analysis.path("redFlags");
        if (flags.isArray() && !flags.isEmpty()) {
            flags.forEach(flag -> System.out.println("• " + flag.asText()));
        } else {
            System.out.println("• No immediate red flags detected");
        }
        System.out.println();

        // 4. Recommendations
        System.out.println("4. Recommended Actions");
        JsonNode recs = analysis.path("recommendations");
        if (recs.isArray() && !recs.isEmpty()) {
            recs.forEach(rec -> System.out.println("• " + rec.asText()));
        } else {
            System.out.println("• No specific recommendations at this time");
        }
        System.out.println();

        System.out.println("Analysis successfully saved to database for claim ID: " + r.getClaimId());
        System.out.println("=".repeat(60) + "\n");
    }
}
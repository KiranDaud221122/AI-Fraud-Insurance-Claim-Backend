package com.fraudclaimdetector.claim_submission_service.controller;

import com.fraudclaimdetector.claim_submission_service.entity.Claim;
import com.fraudclaimdetector.claim_submission_service.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam MultipartFile file) {

        // Fallback if header missing (for testing / direct call)
        String userId = userIdHeader != null && !userIdHeader.trim().isEmpty()
                ? userIdHeader.trim()
                : "test-user-" + System.currentTimeMillis();

        log.debug("Processing upload | userId: {}, file: {}", userId, file.getOriginalFilename());

        try {
            String claimId = claimService.submitClaim(file, userId);
            log.info("Upload success | claimId={}, userId={}", claimId, userId);
            return ResponseEntity.ok(claimId);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Claim>> getUserClaims(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {

        String userId = userIdHeader != null && !userIdHeader.trim().isEmpty()
                ? userIdHeader.trim()
                : "test-user-" + System.currentTimeMillis();

        List<Claim> claims = claimService.getClaimsByUser(userId);

        return claims.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(claims);
    }
}
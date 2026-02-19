package com.fraudclaimdetector.claim_user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String userId;
    private String email;
    private String fullName;
    private String phoneNumber;
}

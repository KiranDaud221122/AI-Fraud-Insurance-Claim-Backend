package com.fraudclaimdetector.claim_fraud_ai_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ClaimFraudAiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimFraudAiServiceApplication.class, args);
    }
}
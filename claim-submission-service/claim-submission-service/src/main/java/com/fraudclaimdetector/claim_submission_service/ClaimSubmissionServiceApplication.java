package com.fraudclaimdetector.claim_submission_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ClaimSubmissionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimSubmissionServiceApplication.class, args);
    }
}
package com.fraudclaimdetector.claim_submission_service.service;

import com.fraudclaimdetector.claim_submission_service.event.ClaimSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, ClaimSubmittedEvent> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topic;

    public void sendClaimSubmittedEvent(ClaimSubmittedEvent event) {
        kafkaTemplate.send(topic, event.getClaimId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka event published | claimId: {}", event.getClaimId());
                    } else {
                        log.error("Kafka publish failed | claimId: {}", event.getClaimId(), ex);
                    }
                });
    }
}
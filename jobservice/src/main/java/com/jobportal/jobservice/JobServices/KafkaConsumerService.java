package com.jobportal.jobservice.JobServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.jobservice.Entity.ProcessedEvent;
import com.jobportal.jobservice.JobPostRepository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * The type Kafka consumer service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JobService jobService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Handle application submitted event.
     *
     * @param messagePayload the message payload
     */
    @KafkaListener(topics = "${app.kafka.topics.job-application}",
            groupId = "${app.kafka.group-id}")
    @Transactional // <--- CRITICAL: Database changes roll back if anything fails
    public void handleApplicationSubmittedEvent(String messagePayload) {
        try {
            // 1. Parse the JSON: {"eventId":123, "jobId":456}
            JsonNode node = objectMapper.readTree(messagePayload);
            Long eventId = node.get("eventId").asLong();
            Long jobId = node.get("jobId").asLong();

            // 2. IDEMPOTENCY CHECK
            if (processedEventRepository.existsByEventId(eventId)) {
                log.info("Duplicate event detected (Event ID: {}). Skipping.", eventId);
                return; // Stop processing immediately
            }

            // 3. Process the business logic
            jobService.incrementApplicationCount(jobId);

            // 4. Mark as processed (Save the ID)
            ProcessedEvent processed = new ProcessedEvent(eventId, jobId, LocalDateTime.now());
            processedEventRepository.save(processed);

            log.info("Successfully processed Event ID: {} for Job ID: {}", eventId, jobId);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}. Error: {}", messagePayload, e.getMessage());
            // Throwing exception here forces Kafka to retry later
            throw new RuntimeException(e);
        }
    }
}

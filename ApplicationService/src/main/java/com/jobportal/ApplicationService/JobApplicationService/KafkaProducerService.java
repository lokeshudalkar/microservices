package com.jobportal.ApplicationService.JobApplicationService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String , String> kafkaTemplate;
    private static final String JOB_APPLIED_TOPIC = "job-application-events";

    public void sendApplicationSubmittedEvent(Long jobId) throws InterruptedException , ExecutionException{
        try {
            kafkaTemplate.send(JOB_APPLIED_TOPIC, String.valueOf(jobId)).get();
            log.info("Published Job Applied Event for Job ID: " + jobId);
        } catch (InterruptedException | ExecutionException e) {
            log.error("FAILED to publish event for Job ID: " + jobId + ". Error: " + e.getMessage());

            throw e;
        }

    }
}

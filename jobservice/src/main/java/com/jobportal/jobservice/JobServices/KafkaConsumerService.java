package com.jobportal.jobservice.JobServices;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JobService jobService;
    private static final String JOB_APPLIED_TOPIC = "job-application-events";

    @KafkaListener(topics = JOB_APPLIED_TOPIC , groupId = "job_service_group")
    public void handleApplicationSubmittedEvent(String jobIdString){
        Long jobId = Long.valueOf(jobIdString);

        try {
            jobService.incrementApplicationCount(jobId);
        } catch (Exception e) {
            System.err.println("Error processing Kafka message for Job ID: " + jobId + ". Error: " + e.getMessage());
        }
    }
}

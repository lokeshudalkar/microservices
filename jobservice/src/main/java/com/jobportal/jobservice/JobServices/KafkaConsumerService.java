package com.jobportal.jobservice.JobServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JobService jobService;


    @KafkaListener(topics = "${app.kafka.topics.job-application}" ,
                    groupId = "${app.kafka.group-id}")
    public void handleApplicationSubmittedEvent(String jobIdString){
        Long jobId = Long.valueOf(jobIdString);

        try {
            jobService.incrementApplicationCount(jobId);
        } catch (Exception e) {
            log.error("Error processing Kafka message for Job ID: " + jobId + ". Error: " + e.getMessage());
        }
    }
}

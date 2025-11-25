package com.jobportal.ApplicationService.JobApplicationService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String , String> kafkaTemplate;

    @Value("${app.kafka.topics.job-application}")
    private  String JOB_APPLIED_TOPIC;

    public void sendApplicationSubmittedEvent(Long eventId , Long jobId) throws InterruptedException , ExecutionException{
        try {
            String payload = String.format("{\"eventId\":%d, \"jobId\":%d}", eventId, jobId);

            kafkaTemplate.send(JOB_APPLIED_TOPIC, String.valueOf(jobId) , payload).get();
            log.info("Published Job Applied Event for Job ID: " + jobId);
        } catch (InterruptedException | ExecutionException e) {
            log.error("FAILED to publish event ID: {}. Error: {}", eventId, e.getMessage());
            throw e;
        }

    }
}

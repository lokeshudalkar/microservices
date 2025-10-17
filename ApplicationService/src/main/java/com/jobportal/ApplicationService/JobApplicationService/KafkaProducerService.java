package com.jobportal.ApplicationService.JobApplicationService;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String , String> kafkaTemplate;
    private static final String JOB_APPLIED_TOPIC = "job-application-events";

    public void sendApplicationSubmittedEvent(Long jobId){
        kafkaTemplate.send(JOB_APPLIED_TOPIC , String.valueOf(jobId));

        System.out.println("Published Job Applied Event for Job ID: " + jobId);
    }
}

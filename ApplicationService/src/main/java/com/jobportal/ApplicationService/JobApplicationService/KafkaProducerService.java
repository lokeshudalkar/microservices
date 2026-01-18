package com.jobportal.ApplicationService.JobApplicationService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


/**
 * The type Kafka producer service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topics.job-application}")
    private String JOB_APPLIED_TOPIC;

    /**
     * Send application submitted event.
     *
     * @param eventId the event id
     * @param jobId   the job id
     */
    public void sendApplicationSubmittedEvent(Long eventId, Long jobId) {

        String payload = String.format("{\"eventId\":%d, \"jobId\":%d}", eventId, jobId);

        kafkaTemplate.send(JOB_APPLIED_TOPIC, String.valueOf(jobId), payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published event for Job ID: {}", jobId);
                    } else {
                        log.error("Failed to publish event: {}", ex.getMessage());
                    }
                });


    }
}

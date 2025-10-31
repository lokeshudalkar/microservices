package com.jobportal.ApplicationService.JobApplicationService;

import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.enums.EventStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPoller {

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelay = 5000) // 5 seconds
    @Transactional
    public void pollAndPublishEvents(){
        log.info("Polling for PENDING outbox events...");

        List<Events> events = outboxEventRepository.findTop100ByStatus(EventStatus.PENDING);
        if(events.isEmpty()) return;

        log.info("Found  PENDING events to publish.", events.size());

        for (Events event : events){
            try {
                kafkaProducerService.sendApplicationSubmittedEvent(Long.valueOf(event.getPayload()));

                event.setStatus(EventStatus.SENT);
                outboxEventRepository.save(event);

            } catch (Exception e) {
                log.warn("Failed to send event {}. Will retry. Error: {}", event.getId(), e.getMessage());
            }
        }
    }


}

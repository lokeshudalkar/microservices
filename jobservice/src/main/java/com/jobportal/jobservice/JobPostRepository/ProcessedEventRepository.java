package com.jobportal.jobservice.JobPostRepository;

import com.jobportal.jobservice.Entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Processed event repository.
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    /**
     * Exists by event id boolean.
     *
     * @param eventId the event id
     * @return the boolean
     */
    boolean existsByEventId(Long eventId);
}
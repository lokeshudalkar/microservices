package com.jobportal.jobservice.JobPostRepository;

import com.jobportal.jobservice.Entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByEventId(Long eventId);
}
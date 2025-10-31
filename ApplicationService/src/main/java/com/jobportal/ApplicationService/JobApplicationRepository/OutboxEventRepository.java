package com.jobportal.ApplicationService.JobApplicationRepository;

import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<Events , Long> {
    List<Events> findTop100ByStatus(EventStatus status);
}

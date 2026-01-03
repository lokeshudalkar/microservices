package com.jobportal.jobservice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    private Long eventId;

    private Long jobId;
    private LocalDateTime processedAt;
}

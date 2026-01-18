package com.jobportal.ApplicationService.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Job application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "job_applications", indexes = {

        @Index(name = "idx_seeker_id", columnList = "seekerId"),

        @Index(name = "idx_jobpost_id", columnList = "jobPostId")
})
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private String resumeUrl;

    private LocalDateTime appliedAt;

    private Long jobPostId; // it will come from job service

    private Long seekerId; // it will come from user-service

}

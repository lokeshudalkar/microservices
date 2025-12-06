package com.jobportal.ApplicationService.Entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

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

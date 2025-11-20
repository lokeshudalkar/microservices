package com.jobportal.jobservice.JobPostDTOs;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    private Long applicationId;

    private String resumeUrl;

    private LocalDateTime appliedAt;

    private Long jobPostId;

    private Long seekerId;
}

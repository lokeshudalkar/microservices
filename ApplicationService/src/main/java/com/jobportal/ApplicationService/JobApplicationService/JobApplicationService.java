package com.jobportal.ApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    public void applyToJob(Long seekerId , JobApplicationDto jobApplicationDto , Long jobId ){

        boolean alreadyApplied = jobApplicationRepository
        .existsBySeekerIdAndJobPostId(seekerId, jobId);

    if (alreadyApplied) {
        throw new IllegalStateException("You have already applied to this job.");
    }

        JobApplication jobApplication = JobApplication.builder()
        .resumeUrl(jobApplicationDto.getResumeUrl())
        .appliedAt(LocalDateTime.now())
        .jobPostId(jobId)
        .seekerId(seekerId)
        .build();
       jobApplicationRepository.save(jobApplication);
    }
}

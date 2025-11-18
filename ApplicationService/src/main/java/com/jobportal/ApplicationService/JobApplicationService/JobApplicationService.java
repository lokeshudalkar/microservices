package com.jobportal.ApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.enums.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final OutboxEventRepository outboxEventRepository;



    @Transactional
    public void applyToJob(Long seekerId , JobApplicationDto jobApplicationDto , Long jobId ){

        //check if link is empty or not
        if(!StringUtils.hasText(jobApplicationDto.getResumeUrl())){
            throw new IllegalStateException("Resume url cannot be empty");
        }

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

        Events events = Events.builder()
                .topic("job-application-events")
                .messageKey(String.valueOf(jobId))
                .payload(String.valueOf(jobId))
                .status(EventStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        outboxEventRepository.save(events);

    }

    @Async("virtualThreadExecutor")
    @Transactional
    public CompletableFuture<Void> applyToJobAsync(Long seekerId , JobApplicationDto jobApplicationDto , Long jobId ){

        //check if link is empty or not
        if(!StringUtils.hasText(jobApplicationDto.getResumeUrl())){
            throw new IllegalStateException("Resume url cannot be empty");
        }

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

        Events events = Events.builder()
                .topic("job-application-events")
                .messageKey(String.valueOf(jobId))
                .payload(String.valueOf(jobId))
                .status(EventStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        outboxEventRepository.save(events);
        return CompletableFuture.completedFuture(null);
    }
}

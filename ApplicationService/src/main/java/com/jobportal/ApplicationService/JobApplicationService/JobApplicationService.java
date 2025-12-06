package com.jobportal.ApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.enums.EventStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final UserClient userClient;
    private final JobPostClient jobPostClient;
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
    public void validateApplication(Long seekerId, Long jobId) {
        boolean alreadyApplied = jobApplicationRepository.existsBySeekerIdAndJobPostId(seekerId, jobId);
        if (alreadyApplied) {
            throw new IllegalStateException("You have already applied to this job.");
        }
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

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "getSeekerIdFallback")
    public Long getSeekerIdByEmail(String email) {
        return userClient.getSeekerId(email);
    }

    // 3. Fallback Method
    public Long getSeekerIdFallback(String email) {
        log.error("User Service is down. Cannot fetch Seeker ID for email: " + email);
        return null;
    }

    @CircuitBreaker(name = "jobServiceBreaker", fallbackMethod = "validateJobFallback")
    public void validateJobExists(Long jobId) {
        jobPostClient.getJobId(jobId);
    }

    // 3. Smart Fallback
    public void validateJobFallback(Long jobId , Throwable t) {
        // If the error is actually "404 Not Found", we want to re-throw it
        // because that is a valid business scenario, not a system failure.
        if (t instanceof feign.FeignException.NotFound) {
            throw (feign.FeignException.NotFound) t;
        }

        // For everything else (Timeouts, 500 errors, Circuit Open),
        // we throw a specific "Service Unavailable" message.
        throw new RuntimeException("JOB_SERVICE_DOWN");
    }
}

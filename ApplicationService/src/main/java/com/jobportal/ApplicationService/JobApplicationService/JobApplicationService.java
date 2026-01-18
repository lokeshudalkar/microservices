package com.jobportal.ApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.enums.EventStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * The type Job application service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final UserClient userClient;
    private final JobPostClient jobPostClient;
    private final JobApplicationRepository jobApplicationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final FileStorageService fileStorageService;


    /**
     * Validate application.
     *
     * @param seekerId the seeker id
     * @param jobId    the job id
     */
//    @Transactional
//    public void applyToJob(Long seekerId , JobApplicationDto jobApplicationDto , Long jobId ){
//
//        //check if link is empty or not
//        if(!StringUtils.hasText(jobApplicationDto.getResumeUrl())){
//            throw new IllegalStateException("Resume url cannot be empty");
//        }
//
//        boolean alreadyApplied = jobApplicationRepository
//        .existsBySeekerIdAndJobPostId(seekerId, jobId);
//
//        if (alreadyApplied) {
//            throw new IllegalStateException("You have already applied to this job.");
//        }
//
//        JobApplication jobApplication = JobApplication.builder()
//        .resumeUrl(jobApplicationDto.getResumeUrl())
//        .appliedAt(LocalDateTime.now())
//        .jobPostId(jobId)
//        .seekerId(seekerId)
//        .build();
//       jobApplicationRepository.save(jobApplication);
//
//        Events events = Events.builder()
//                .topic("job-application-events")
//                .messageKey(String.valueOf(jobId))
//                .payload(String.valueOf(jobId))
//                .status(EventStatus.PENDING)
//                .createdAt(LocalDateTime.now())
//                .build();
//        outboxEventRepository.save(events);
//
//    }
    public void validateApplication(Long seekerId, Long jobId) {
        boolean alreadyApplied = jobApplicationRepository.existsBySeekerIdAndJobPostId(seekerId, jobId);
        if (alreadyApplied) {
            throw new IllegalStateException("You have already applied to this job.");
        }
    }

    /**
     * Apply to job async completable future.
     *
     * @param seekerId the seeker id
     * @param resume   the resume
     * @param jobId    the job id
     * @return the completable future
     */
    @Async("virtualThreadExecutor")
    @Transactional
    public CompletableFuture<Void> applyToJobAsync(Long seekerId, String resume, Long jobId) {
        log.info("Starting async job application for seeker: {} and job: {}", seekerId, jobId);
        //check if link is empty or not
        if (resume.isEmpty() || resume == null) {
            throw new IllegalStateException("Resume file cannot be empty");
        }

        boolean alreadyApplied = jobApplicationRepository
                .existsBySeekerIdAndJobPostId(seekerId, jobId);

        if (alreadyApplied) {
            throw new IllegalStateException("You have already applied to this job.");
        }
        try {
            JobApplication jobApplication = JobApplication.builder()
                    .resumeUrl(resume)
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
        } catch (RuntimeException e) {
            log.error("CRITICAL: Transaction rolling back because file save failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store resume file", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Gets seeker id by email.
     *
     * @param email the email
     * @return the seeker id by email
     */
    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "getSeekerIdFallback")
    public Long getSeekerIdByEmail(String email) {
        return userClient.getSeekerId(email);
    }

    /**
     * Gets seeker id fallback.
     *
     * @param email the email
     * @param t     the t
     * @return the seeker id fallback
     */
// 3. Fallback Method
    public Long getSeekerIdFallback(String email, Throwable t) {
        log.error("User Service is down. Cannot fetch Seeker ID for email: {}", email);
        return null;
    }

    /**
     * Validate job exists.
     *
     * @param jobId the job id
     */
    @CircuitBreaker(name = "jobServiceBreaker", fallbackMethod = "validateJobFallback")
    public void validateJobExists(Long jobId) {
        jobPostClient.getJobId(jobId);
    }

    /**
     * Validate job fallback.
     *
     * @param jobId the job id
     * @param t     the t
     */
// 3. Smart Fallback
    public void validateJobFallback(Long jobId, Throwable t) {
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

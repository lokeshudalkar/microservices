package com.jobportal.ApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.Exception.AlreadyAppliedException;
import com.jobportal.ApplicationService.Exception.FileUploadException;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.enums.EventStatus;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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


    /**
     * Apply to job async completable future.
     *
     * @param resume the resume
     * @param jobId  the job id
     */

    @Transactional
    public void applyToJob(MultipartFile resume, Long jobId, String email) {

        validateJobExists(jobId);
        Long seekerId = getSeekerIdByEmail(email);
        if (jobApplicationRepository.existsBySeekerIdAndJobPostId(seekerId, jobId)) {
            throw new AlreadyAppliedException("You already applied to this job");
        }
        JobApplication jobApplication = JobApplication.builder()

                .appliedAt(LocalDateTime.now())
                .jobPostId(jobId)
                .seekerId(seekerId)
                .build();
        jobApplicationRepository.save(jobApplication);
        uploadResumeAsync(resume, jobApplication.getApplicationId());
        publishEventAsync(jobId);

    }


    @Transactional
    public void uploadResumeAsync(MultipartFile resume, Long applicationId) {
        try {
            String path = fileStorageService.saveFile(resume);
            jobApplicationRepository.updateResume(applicationId, path);
        } catch (Exception e) {
            throw new FileUploadException("FILE_UPLOAD_FAILED");
        }
    }


    public void publishEventAsync(Long jobId) {
        Events events = Events.builder()
                .topic("job-application-events")
                .messageKey(String.valueOf(jobId))
                .payload(String.valueOf(jobId))
                .status(EventStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        outboxEventRepository.save(events);
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
        if (t instanceof FeignException.NotFound) {
            throw new RuntimeException("USER_NOT_FOUND");
        }
        throw new RuntimeException("USER_SERVICE_DOWN", t);
    }

    /**
     * Validate job exists.
     *
     * @param jobId the job id
     */
    @CircuitBreaker(name = "jobServiceBreaker", fallbackMethod = "validateJobFallback")
    public void validateJobExists(Long jobId) {
        jobPostClient.getJobId(jobId);
        //If Job exists then it does nothing
        //if job do not exist then is throws exception
        //That exception is thrown in fallBack Method
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

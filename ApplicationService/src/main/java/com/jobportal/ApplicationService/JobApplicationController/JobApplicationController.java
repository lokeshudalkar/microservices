package com.jobportal.ApplicationService.JobApplicationController;

import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationService.FileStorageService;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;
import com.jobportal.ApplicationService.JobApplicationService.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * The type Job application controller.
 */
@RestController
@RequestMapping("/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    private final UserClient userClient;

    private final KafkaProducerService kafkaProducerService;

    private final JobPostClient jobPostClient;

    private final JobApplicationRepository jobApplicationRepository;

    private final FileStorageService fileStorageService;


    /**
     * Apply response entity.
     *
     * @param email  the email
     * @param role   the role
     * @param resume the resume
     * @param jobId  the job id
     * @return the response entity
     */
    @PostMapping(value = "/apply-to/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> apply(@RequestHeader("X-User-Email") String email,
                                        @RequestHeader("X-User-Role") String role,
                                        @RequestPart("resume") MultipartFile resume,
                                        @PathVariable Long jobId) {

        if (!"SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not Seeker");
        }

        //validate file type
        if (!"application/pdf".equals(resume.getContentType())) {
            return ResponseEntity.badRequest().body("Only PDF files are allowed");
        }

        //circuit breaker method
        Long seekerId = jobApplicationService.getSeekerIdByEmail(email);

        if (seekerId == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Cannot apply. User Service is currently unavailable.");
        }
        try {
            jobApplicationService.validateJobExists(jobId);
        } catch (feign.FeignException.NotFound e) {
            // Scenario 1: Job Service is UP, but Job ID is wrong.
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job With Id " + jobId + " Not Found");
        } catch (RuntimeException e) {
            // Scenario 2: Job Service is DOWN (Circuit Breaker Open or Timeout).
            if ("JOB_SERVICE_DOWN".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Unable to verify Job. Job Service is currently unavailable.");
            }
            throw e; // unexpected error
        }
        String filePath;
        try {
            filePath = fileStorageService.saveFile(resume);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to save resume");
        }
        jobApplicationService.validateApplication(seekerId, jobId);
        jobApplicationService.applyToJobAsync(seekerId, filePath, jobId);

//        jobPostClient.incrementApplicationCount(jobId);
//        kafkaProducerService.sendApplicationSubmittedEvent(jobId);
        return ResponseEntity.accepted().body("Application Submitted Successfully");
    }

    /**
     * Gets my applications.
     *
     * @param email the email
     * @param role  the role
     * @return the my applications
     */
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(@RequestHeader("X-User-Email") String email,
                                               @RequestHeader("X-User-Role") String role) {
        if (!"SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //circuit breaker method
        Long seekerId = jobApplicationService.getSeekerIdByEmail(email);

        if (seekerId == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Cannot apply. User Service is currently unavailable.");
        }

        List<JobApplication> applications = jobApplicationRepository.findBySeekerId(seekerId);

        return ResponseEntity.ok(applications);
    }
}

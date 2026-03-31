package com.jobportal.ApplicationService.JobApplicationController;

import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;
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


import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;


/**
 * The type Job application controller.
 */
@RestController
@RequestMapping("/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {


    private final JobApplicationService jobApplicationService;


    private final JobApplicationRepository jobApplicationRepository;


    /**
     * Apply async response entity.
     *
     * @param email  the email
     * @param role   the role
     * @param resume the resume
     * @param jobId  the job id
     * @return the response entity
     */
    @PostMapping(value = "/apply-to-job/{job-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> applyAsync(@RequestHeader("X-User-Email") String email,
                                             @RequestHeader("X-User-Role") String role,
                                             @RequestPart("resume") MultipartFile resume,
                                             @PathVariable("job-id") Long jobId) {
        if (!role.equals("SEEKER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("ONLY SEEKERS CAN APPLY TO JOB POSTINGS");
        }

        jobApplicationService.applyToJob(resume, jobId, email);
        return ResponseEntity.accepted().body("Application Submitted Successfully");

    }


//    @PostMapping(value = "/apply-to/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> apply(@RequestHeader("X-User-Email") String email,
//                                        @RequestHeader("X-User-Role") String role,
//                                        @RequestPart("resume") MultipartFile resume,
//                                        @PathVariable Long jobId) {
//
//        if (!"SEEKER".equals(role)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not Seeker");
//        }
//
//        //validate file type
//        if (!"application/pdf".equals(resume.getContentType())) {
//            return ResponseEntity.badRequest().body("Only PDF files are allowed");
//        }
//
//        //circuit breaker method
//        Long seekerId = jobApplicationService.getSeekerIdByEmail(email);
//
//        if (seekerId == null) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                    .body("Cannot apply. User Service is currently unavailable.");
//        }
//        try {
//            jobApplicationService.validateJobExists(jobId);
//        } catch (feign.FeignException.NotFound e) {
//            // Scenario 1: Job Service is UP, but Job ID is wrong.
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Job With Id " + jobId + " Not Found");
//        } catch (RuntimeException e) {
//            // Scenario 2: Job Service is DOWN (Circuit Breaker Open or Timeout).
//            if ("JOB_SERVICE_DOWN".equals(e.getMessage())) {
//                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                        .body("Unable to verify Job. Job Service is currently unavailable.");
//            }
//            throw e; // unexpected error
//        }
//        String filePath;
//        try {
//            filePath = fileStorageService.saveFile(resume);
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body("Failed to save resume");
//        }
//        jobApplicationService.validateApplication(seekerId, jobId);
//        jobApplicationService.applyToJobAsync(seekerId, filePath, jobId);
//
//        jobPostClient.incrementApplicationCount(jobId);
//        kafkaProducerService.sendApplicationSubmittedEvent(jobId);
//        return ResponseEntity.accepted().body("Application Submitted Successfully");
//    }

    /**
     * Gets my applications.
     *
     * @param email the email
     * @param role  the role
     * @return my applications
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


    @GetMapping("/debug/thread-info")
    public Map<String, Object> threadInfo() {
        Thread t = Thread.currentThread();
        return Map.of(
                "name", t.getName(),
                "isVirtual", t.isVirtual(),
                "threadId", t.threadId()
        );
    }
}
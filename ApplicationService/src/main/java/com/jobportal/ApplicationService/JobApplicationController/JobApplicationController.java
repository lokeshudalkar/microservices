package com.jobportal.ApplicationService.JobApplicationController;

import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.JobApplicationService.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private  final JobApplicationService jobApplicationService;

    private  final UserClient userClient;

    private final KafkaProducerService kafkaProducerService;

    private final JobPostClient jobPostClient;

    private final JobApplicationRepository jobApplicationRepository;


    @PostMapping(value = "/apply-to/{jobId}" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> apply(@RequestHeader("X-User-Email") String email,
                                   @RequestHeader("X-User-Role") String role,
                                   @RequestPart("resume")  MultipartFile resume,
                                   @PathVariable Long jobId){

        if(!"SEEKER".equals(role)){
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
        }catch (feign.FeignException.NotFound e) {
            // Scenario 1: Job Service is UP, but Job ID is wrong.
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job With Id " + jobId + " Not Found");
        }catch (RuntimeException e) {
            // Scenario 2: Job Service is DOWN (Circuit Breaker Open or Timeout).
            if ("JOB_SERVICE_DOWN".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Unable to verify Job. Job Service is currently unavailable.");
            }
            throw e; // unexpected error
        }
        jobApplicationService.validateApplication(seekerId, jobId);
        jobApplicationService.applyToJobAsync(seekerId ,
                resume , jobId);

//        jobPostClient.incrementApplicationCount(jobId);
//        kafkaProducerService.sendApplicationSubmittedEvent(jobId);
        return ResponseEntity.accepted().body("Application Submitted Successfully");
    }

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

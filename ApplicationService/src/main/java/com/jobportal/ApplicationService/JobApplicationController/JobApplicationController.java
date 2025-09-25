package com.jobportal.ApplicationService.JobApplicationController;


import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job-applications")
//@RequiredArgsConstructor
public class JobApplicationController {


    private final   JobApplicationService jobApplicationService;

    private final JobApplicationRepository jobApplicationRepository;

    private final UserClient userClient;
    public JobApplicationController(JobApplicationService jobApplicationService,
                                    JobApplicationRepository jobApplicationRepository,
                                    UserClient userClient) {
        this.jobApplicationService = jobApplicationService;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userClient = userClient;
    }

    @PostMapping("/apply-to/{jobId}")
    public ResponseEntity<?> apply(@RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @RequestBody JobApplicationDto jobApplicationDto ,
               @PathVariable Long jobId){

        if(!"SEEKER".equals(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not Seeker");
        }

        jobApplicationService.applyToJob(userClient.getSeekerId(email),
                jobApplicationDto , jobId);

        return new ResponseEntity<>(null , HttpStatus.CREATED);
    }
}

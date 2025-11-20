package com.jobportal.ApplicationService.JobApplicationController;


import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.JobApplicationService.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



    @PostMapping("/apply-to/{jobId}")
    public ResponseEntity<?> apply(@RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @RequestBody JobApplicationDto jobApplicationDto ,
               @PathVariable Long jobId){

        if(!"SEEKER".equals(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not Seeker");
        }
        try {
            jobPostClient.getJobId(jobId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job With Id " + jobId + " Not Found");
        }
        jobApplicationService.applyToJobAsync(userClient.getSeekerId(email),
                jobApplicationDto , jobId);

//        jobPostClient.incrementApplicationCount(jobId);
//        kafkaProducerService.sendApplicationSubmittedEvent(jobId);
        return ResponseEntity.accepted().body("Application Submitted Successfully");
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<JobApplication>> getMyApplications(@RequestHeader("X-User-Email") String email,
                                                                  @RequestHeader("X-User-Role") String role) {
        if (!"SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long seekerId = userClient.getSeekerId(email);

        List<JobApplication> applications = jobApplicationRepository.findBySeekerId(seekerId);

        return ResponseEntity.ok(applications);
    }
}

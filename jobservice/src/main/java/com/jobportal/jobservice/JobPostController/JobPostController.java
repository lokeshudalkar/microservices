package com.jobportal.jobservice.JobPostController;

import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostDTOs.User;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import com.jobportal.jobservice.feignClient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobPostController {

    private final JobService jobService;
    private final JobPostRepository jobPostRepository;
    private final UserClient userClient;

    @PostMapping("/post")
    public ResponseEntity<?> createJob(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @RequestBody JobPostRequest jobPostRequest) {

        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter.");
        }

        User recruiter = userClient.getUserByEmail(email);
        if (recruiter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recruiter not found.");
        }

        JobPost jobPost = jobService.createJobPost(jobPostRequest, recruiter.getId());
        return new ResponseEntity<>(jobPost, HttpStatus.CREATED);
    }


    @PutMapping("/updatejob/{jobId}")
    public ResponseEntity<?> updateJob(@RequestBody JobPostRequest jobPostRequest ,
                                       @RequestHeader("X-User-Email") String email ,
                                        @PathVariable Long jobId){

        String recruiterEmail = email;

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }

        JobPost jobPost = jobService.updateJob(jobId , jobPostRequest , recruiter.getId());
        return ResponseEntity.ok(jobPost);
    }

    @DeleteMapping("/delete-job/{jobId}")
    public ResponseEntity<?> deleteJob(@RequestHeader("X-User-Email") String email ,
                                        @PathVariable Long jobId){

        String recruiterEmail = email;

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        if(jobService.deleteJob(jobId , recruiter.getId())){
            return ResponseEntity.ok().body("Job is successfully deleted");
        }
        return ResponseEntity.badRequest().body("your not allowed to delete this job");
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<?> getAllJobPostedByRecruiter(@RequestHeader("X-User-Email") String email ){

        String recruiterEmail = email;

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        return ResponseEntity.ok(jobPostRepository.findByRecruiterId(recruiter.getId()));
    }
}

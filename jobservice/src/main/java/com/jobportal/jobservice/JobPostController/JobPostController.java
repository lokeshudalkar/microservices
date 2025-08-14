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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-post")
@RequiredArgsConstructor
public class JobPostController {

    private final JobService jobService;
    private final JobPostRepository jobPostRepository;
    private final UserClient userClient;

    @PostMapping("/post")
    public ResponseEntity<?> createJob(@RequestBody JobPostRequest jobPostRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String recruiterEmail = authentication.getName();

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        JobPost jobPost = jobService.createJobPost(jobPostRequest , recruiter.getId());
        return new ResponseEntity<>(jobPost, HttpStatus.CREATED);
    }

    @PutMapping("/updatejob/{jobId}")
    public ResponseEntity<?> updateJob(@RequestBody JobPostRequest jobPostRequest , @PathVariable Long jobId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String recruiterEmail = authentication.getName();

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }

        JobPost jobPost = jobService.updateJob(jobId , jobPostRequest , recruiter.getId());
        return ResponseEntity.ok(jobPost);
    }

    @DeleteMapping("/delete-job/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String recruiterEmail = authentication.getName();

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        if(jobService.deleteJob(jobId , recruiter.getId())){
            return ResponseEntity.ok().body("Job is successfully deleted");
        }
        return ResponseEntity.badRequest().body("your not allowed to delete this job");
    }

    @GetMapping
    public ResponseEntity<?> getAllJobPostedByRecruiter(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String recruiterEmail = authentication.getName();

        User recruiter = userClient.getUserByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        return ResponseEntity.ok(jobPostRepository.findByRecruiterId(recruiter.getId()));
    }
}

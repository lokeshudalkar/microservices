package com.jobportal.jobservice.JobPostController;

import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import com.jobportal.jobservice.feignClient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job-post")
@RequiredArgsConstructor
public class JobPostController {

    private final JobService jobService;
    private final JobPostRepository jobPostRepository;
    private final UserClient userClient;

    @PostMapping("/post")
    public ResponseEntity<?> createJob(@RequestBody JobPostRequest jobPostRequest){
        Long recruiterIdFromUserService = userClient.getRecruiterId(jobPostRequest.getRecruiterId());
        if (recruiterIdFromUserService == 0L) {
            return ResponseEntity.badRequest().body("User is not a recruiter or does not exist");
        }
        JobPost jobPost = jobService.createJobPost(jobPostRequest , recruiterIdFromUserService);
        return new ResponseEntity<>(jobPost, HttpStatus.CREATED);
    }
}

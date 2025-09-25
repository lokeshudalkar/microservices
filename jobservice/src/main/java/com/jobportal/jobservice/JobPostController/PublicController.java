package com.jobportal.jobservice.JobPostController;


import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private  final JobPostRepository jobPostRepository;

    @GetMapping
    public ResponseEntity<?> getAllJobPost(){
        return ResponseEntity.ok(jobPostRepository.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJobsByKeyword(@RequestParam(required = false)  String keyword){
        List<JobPost> jobs;
        if(StringUtils.hasText(keyword)){
            jobs = jobPostRepository
                    .findByTitleContainingOrDescriptionContainingOrLocationContainingAllIgnoreCase(
                            keyword, keyword, keyword
                    );
        }
        else {
            jobs = jobPostRepository.findAll();
        }
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{job-id}/job-id")
    public Long getJobId(@PathVariable Long jobId){
        JobPost jobPost = jobPostRepository.findById(jobId).orElseThrow(
                () -> new RuntimeException("Job With this id is not found")
        );

        return jobPost.getId();
    }

}

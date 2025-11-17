package com.jobportal.jobservice.JobPostController;


import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private  final JobPostRepository jobPostRepository;
    private final JobService jobService;

    @GetMapping
    @Cacheable(value = "allJobs" , key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<JobPost> getAllJobPost(Pageable pageable){
        return jobPostRepository.findAll((org.springframework.data.domain.Pageable) pageable);
    }

    @GetMapping("/search")
    @Cacheable(value = "jobsearch", key = "(#keyword != null ? #keyword : 'all') + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<JobPost> searchJobsByKeyword(@RequestParam(required = false)  String keyword , Pageable pageable){
        Page<JobPost> jobs;
        if(StringUtils.hasText(keyword)){
            jobs = jobPostRepository
                    .findByTitleContainingOrDescriptionContainingOrLocationContainingAllIgnoreCase(
                            keyword, keyword, keyword , pageable
                    );
        }
        else {
            jobs = jobPostRepository.findAll((org.springframework.data.domain.Pageable) pageable);
        }
        return jobs;
    }

    @GetMapping("/{job-id}/job-id")
    public Long getJobId(@PathVariable("job-id") Long jobId){
        JobPost jobPost = jobPostRepository.findById(jobId).orElseThrow(
                () -> new RuntimeException("Job With this id is not found")
        );

        return jobPost.getId();
    }

    @PutMapping("/internal/job/{jobId}/increment-count")
    public int incrementJobCount(@PathVariable Long jobId) {
        jobService.incrementApplicationCount(jobId);
        return 0;
    }
}

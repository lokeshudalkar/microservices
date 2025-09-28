package com.jobportal.ApplicationService.JobApplicationController;


import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/all")
@RequiredArgsConstructor
public class PublicJobApplicationController {

    private final JobApplicationRepository jobApplicationRepository;

    @GetMapping("/job-application/{jobId}")
    public  Long getAllJobsById(@PathVariable("jobId") Long jobId){
        List<JobApplication> byJobPostId = jobApplicationRepository.findByJobPostId(jobId);
        return (long) byJobPostId.size();
    }
}

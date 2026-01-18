package com.jobportal.ApplicationService.JobApplicationController;


import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The type Public job application controller.
 */
@RestController
@RequestMapping("/all")
@RequiredArgsConstructor
public class PublicJobApplicationController {

    private final JobApplicationRepository jobApplicationRepository;

    /**
     * Gets all jobs by id.
     *
     * @param jobId the job id
     * @return the all jobs by id
     */
    @GetMapping("/job-application/{jobId}")
    public Long getAllJobsById(@PathVariable("jobId") Long jobId) {
        List<JobApplication> byJobPostId = jobApplicationRepository.findByJobPostId(jobId);
        return (long) byJobPostId.size();
    }


    /**
     * Delete application of job post.
     *
     * @param jobId the job id
     */
    @DeleteMapping("/delete-applications/{jobId}")
    public void deleteApplicationOfJobPost(@PathVariable("jobId") Long jobId) {
        jobApplicationRepository.deleteByJobPostId(jobId);
    }

    /**
     * Gets applications list by job id.
     *
     * @param jobId the job id
     * @return the applications list by job id
     */
    @GetMapping("/applications-list/{jobId}")
    public List<JobApplication> getApplicationsListByJobId(@PathVariable("jobId") Long jobId) {
        return jobApplicationRepository.findByJobPostId(jobId); //
    }
}

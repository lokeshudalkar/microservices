package com.jobportal.jobservice.feignClient;

import com.jobportal.jobservice.JobPostDTOs.JobApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * The interface Job application client.
 */
@FeignClient(name = "application-service")
public interface JobApplicationClient {

    /**
     * Gets total applications for a job.
     *
     * @param jobId the job id
     * @return the total applications for a job
     */
    @GetMapping("/all/job-application/{jobId}")
    Long getTotalApplicationsForAJob(@PathVariable("jobId") Long jobId);

    /**
     * Delete job application of job post void.
     *
     * @param jobId the job id
     * @return the void
     */
    @DeleteMapping("/all/delete-applications/{jobId}")
    Void deleteJobApplicationOfJobPost(@PathVariable("jobId") Long jobId);

    /**
     * Gets applications for job.
     *
     * @param jobId the job id
     * @return the applications for job
     */
    @GetMapping("/all/applications-list/{jobId}")
    List<JobApplication> getApplicationsForJob(@PathVariable("jobId") Long jobId);
}

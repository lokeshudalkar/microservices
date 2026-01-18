package com.jobportal.ApplicationService.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * The interface Job post client.
 */
@FeignClient(name = "jobservice")
public interface JobPostClient {

    /**
     * Gets job id.
     *
     * @param id the id
     * @return the job id
     */
    @GetMapping("/public/{job-id}/job-id")
    Long getJobId(@PathVariable("job-id") Long id);


    /**
     * Increment application count.
     *
     * @param jobId the job id
     */
    @PutMapping("/public/internal/job/{jobId}/increment-count")
    void incrementApplicationCount(@PathVariable("jobId") Long jobId);
}

package com.jobportal.jobservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "application-service")
public interface JobApplicationClient {

    @GetMapping("/all/job-application/{jobId}")
    Long getTotalApplicationsForAJob(@PathVariable("jobId") Long jobId);

    @DeleteMapping("/all/delete-applications/{jobId}")
    Void deleteJobApplicationOfJobPost(@PathVariable("jobId") Long jobId);
}

package com.jobportal.ApplicationService.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "jobservice")
public interface JobPostClient {

    @GetMapping("/public/{job-id}/job-id")
    Long getJobId(@PathVariable("job-id") Long id);

}

package com.jobportal.jobservice.feignClient;


import com.jobportal.jobservice.JobPostDTOs.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/{id}/recruiter-id")
    Long getRecruiterId(@PathVariable("id") Long id);

    @GetMapping("/users/by-email/{email}") // Add this new method
    User getUserByEmail(@PathVariable("email") String email);
}

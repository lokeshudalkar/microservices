package com.jobportal.jobservice.feignClient;


import com.jobportal.jobservice.JobPostDTOs.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The interface User client.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    /**
     * Gets recruiter id.
     *
     * @param id the id
     * @return the recruiter id
     */
    @GetMapping("/users/{recruiter-id}/recruiter-id")
    Long getRecruiterId(@PathVariable("recruiter-id") Long id);

    /**
     * Gets user id by email.
     *
     * @param email the email
     * @return the user id by email
     */
    @GetMapping("/users/by-email/{email}")
        // Add this new method
    User getUserIdByEmail(@PathVariable("email") String email);

}

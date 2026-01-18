package com.jobportal.ApplicationService.FeignClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The interface User client.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    /**
     * Gets seeker id.
     *
     * @param email the email
     * @return the seeker id
     */
    @GetMapping("/users/by_email/{email}")
    Long getSeekerId(@PathVariable("email") String email);

}

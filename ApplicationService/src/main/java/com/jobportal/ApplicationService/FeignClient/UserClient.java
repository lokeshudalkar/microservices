package com.jobportal.ApplicationService.FeignClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/by-email/{email}")
    Long getSeekerId(@PathVariable("email") String email);

}

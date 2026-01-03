package com.jobportal.user_service.Controllers;


import com.jobportal.user_service.Entity.Role;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * The type User controller.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * Gets recruiter id.
     *
     * @param id the id
     * @return the recruiter id
     */
    @GetMapping("/{recruiter-id}/recruiter-id")
    public Long getRecruiterId(@PathVariable("recruiter-id") Long id) {
        // You can also check if this user is actually a recruiter before returning
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.RECRUITER) {
            return user.getId();
        }
        return 0L;
    }

    // This Method is for application Service

    /**
     * Gets seeker id.
     *
     * @param email the email
     * @return the seeker id
     */
    @GetMapping("/by_email/{email}") //  Only returns the Long ID (Used by Application Service)
    public Long getSeekerId(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found with this email")
        );
        return user.getId();
    }

    /**
     * Gets user id by email.
     *
     * @param email the email
     * @return the user id by email
     */
//This is for Job Service
    @GetMapping("/by-email/{email}")
    public User getUserIdByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found with this email")
        );

    }
}

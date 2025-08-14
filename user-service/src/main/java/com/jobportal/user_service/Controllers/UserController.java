package com.jobportal.user_service.Controllers;


import com.jobportal.user_service.Entity.Role;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/{id}/recruiter-id")
    public Long getRecruiterId(@PathVariable Long id) {
        // You can also check if this user is actually a recruiter before returning
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.RECRUITER) {
            return user.getId();
        }
        return 0L;
    }

    @GetMapping("/by-email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

package com.jobportal.user_service.Services;


import com.jobportal.user_service.UserDTOs.UserRequest;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * The type User service.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole())
                .build();
        return userRepository.save(user);
    }

}

package com.jobportal.user_service.Controllers;

import com.jobportal.user_service.Repositories.UserRepository;
import com.jobportal.user_service.UserDTOs.AuthRequest;
import com.jobportal.user_service.UserDTOs.UserRequest;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Services.UserDetailServiceImpl;
import com.jobportal.user_service.Services.UserService;
import com.jobportal.user_service.Utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterLoginController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;


    private final UserDetailServiceImpl userDetailService;


    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest userRequest){
        return new ResponseEntity<>(userService.registerUser(userRequest), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String , String>> loginUser(@Valid @RequestBody AuthRequest authRequest){

        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

                String jwt = jwtUtil.generateToken(authRequest.getEmail() , String.valueOf(user.getRole()));

                return ResponseEntity.ok(Map.of("token", jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication failed"));
            }
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Incorrect username or password"));
        }
    }
}


package com.jobportal.jobservice.JobServices;


import com.jobportal.jobservice.JobPostDTOs.User;

import com.jobportal.jobservice.feignClient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserClient userClient;
    @Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userClient.getUserByEmail(email);

    if (user != null) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("") // In JWT auth, password may not be used
                .roles(user.getRole()) // Automatically adds ROLE_ prefix
                .build();
    }
    throw new UsernameNotFoundException("User not found with email: " + email);
}

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userClient.getUserByEmail(email);
//        if (user != null) {
//            // 1. Create a list of authorities from the user's role
//            // The "ROLE_" prefix is a Spring Security convention.
////            List<GrantedAuthority> authorities = Collections.singletonList(
////                new SimpleGrantedAuthority("ROLE_" + user.getRole())
////            );
//
//            // 2. Return a UserDetails object with the correct authorities
//            return new org.springframework.security.core.userdetails.User(
//                    user.getEmail(),
//                    "", // Password is not needed as JWT is already validated
//                    Collections.singletonList(new SimpleGrantedAuthority(("ROLE_" + user.getRole()))
//                    ) // Provide the user's roles to Spring
//                            // Security
//            );
//        }
//        throw new UsernameNotFoundException("User not found with email: " + email);
//    }
}

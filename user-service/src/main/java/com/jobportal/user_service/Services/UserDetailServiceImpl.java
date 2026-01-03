package com.jobportal.user_service.Services;


import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The type User detail service.
 */
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder().username(user.get().getEmail())
                    .password(user.get().getPassword())
                    .roles(String.valueOf(user.get().getRole())).build();
        }
        throw new UsernameNotFoundException("user Not found");
    }
}

package com.jobportal.user_service.serviceTest;


import com.jobportal.user_service.Entity.Role;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import com.jobportal.user_service.Services.UserService;
import com.jobportal.user_service.UserDTOs.UserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {


    @Mock
    private  UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void register_user(){
        UserRequest request = UserRequest.builder().email("test@example.com").name("lucky").password("gfdfsfdb").role(Role.SEEKER).build();
        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("lucky")
                .role(Role.SEEKER)
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("fjf;lsjfo;g");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        User new_user = userService.registerUser(request);

        assertNotNull(new_user);
        assertEquals("test@example.com" , new_user.getEmail());
        assertEquals(1L , new_user.getId());

        verify(userRepository , times(1)).save(any(User.class));
    }

    @Test
    void tryTORegisterUser_whenAlreadyExists(){
        UserRequest request = UserRequest.builder().email("test@example.com").name("lucky").password("gfdfsfdb").role(Role.SEEKER).build();
        User existingUser = User.builder().id(99L).email("test@example.com").build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        Exception exception = assertThrows(RuntimeException.class , () -> userService.registerUser(request));

        assertEquals("Email already exists" , exception.getMessage());

        //confirm that save method is never called
        verify(userRepository, never()).save(any(User.class));
    }
}

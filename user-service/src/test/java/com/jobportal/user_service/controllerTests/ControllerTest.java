package com.jobportal.user_service.controllerTests;

import com.jobportal.user_service.Controllers.RegisterLoginController;
import com.jobportal.user_service.Entity.Role;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import com.jobportal.user_service.Services.UserDetailServiceImpl;
import com.jobportal.user_service.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper
import com.jobportal.user_service.Utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterLoginController.class)
@AutoConfigureMockMvc(addFilters = false) // FIX 1: Turn off Security (Login/CSRF) for this test
@TestPropertySource(properties = "server.port=8081")
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private  JwtUtil jwtUtil;

    @MockitoBean
    private  UserDetailServiceImpl userDetailService;


    @Autowired
    private ObjectMapper objectMapper; // Helper to turn Objects into JSON strings

    @MockitoBean
    private AuthenticationManager authenticationManager;


    @Test
    void registerUser_ShouldReturnCreated() throws Exception {

        String completeJson = "{"
                + "\"email\":\"test@example.com\","
                + "\"password\":\"securePass123\","
                + "\"name\":\"Lucky\","
                + "\"role\":\"SEEKER\""
                + "}";

        when(userService.registerUser(any())).thenReturn(new User());


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completeJson)) // Send full data
                .andExpect(status().isCreated());
    }

    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsAreValid() throws Exception{
        String loginJson = "{"
                + "\"email\":\"test@example.com\","
                + "\"password\":\"securePass123\""
                + "}";

        // Mock the User that will be found in the DB
        User mockUser = User.builder()
                .email("test@example.com")
                .role(Role.SEEKER)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

        when(jwtUtil.generateToken(any() , any())).thenReturn("fake-jwt-token-123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("fake-jwt-token-123"));
    }


    @Test
    void loginUser_ShouldReturnUnauthorized_WhenCredentialsAreWrong() throws Exception {
        String loginJson = "{"
                + "\"email\":\"test@example.com\","
                + "\"password\":\"wrongPassword\""
                + "}";


        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.error").value("Incorrect username or password"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenInputIsInvalid() throws Exception {

        String invalidJson = "{}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }



}

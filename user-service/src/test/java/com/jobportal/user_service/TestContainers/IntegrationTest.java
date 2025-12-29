package com.jobportal.user_service.TestContainers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.user_service.Entity.Role;
import com.jobportal.user_service.Entity.User;
import com.jobportal.user_service.Repositories.UserRepository;
import com.jobportal.user_service.UserDTOs.AuthRequest;
import com.jobportal.user_service.UserDTOs.UserRequest;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class IntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
     void setUp(){
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void register_successfully(){
        UserRequest request = new UserRequest("Lucky", "lucky@test.com", "lucky123", Role.SEEKER);

        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findByEmail("lucky@test.com");
        assertTrue(savedUser.isPresent());

        assertEquals("Lucky", savedUser.get().getName());
        assertEquals(Role.SEEKER, savedUser.get().getRole());

        //check that password is not saved in form of plain text
        assertNotEquals("lucky123" , savedUser.get().getPassword());

    }

    @Test
    void shouldLoginUser_AndReturnToken() throws Exception {

        UserRequest registerRequest = new UserRequest("Lucky", "login@test.com", "password123", Role.RECRUITER);

        // Register the user first so they exist in DB
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 2. Try to login
        AuthRequest loginRequest = new AuthRequest("login@test.com", "password123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn(); // Verify JWT token is returned

        String responseContent = result.getResponse().getContentAsString();

        // Parse the JSON string to get the specific field
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String token = jsonNode.get("token").asText();

        System.out.println("\n==================================");
        System.out.println("EXTRACTED TOKEN: " + token);
        System.out.println("==================================\n");
    }

    @Test
    void shouldFailLogin_WhenPasswordIsWrong() throws Exception {
        // Arrange
        UserRequest registerRequest = new UserRequest("Lucky", "wrongpass@test.com", "password123", Role.SEEKER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login with WRONG password
        AuthRequest badLogin = new AuthRequest("wrongpass@test.com", "wrongPassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized()); // Expect 401
    }

}

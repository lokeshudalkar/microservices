package com.jobportal.jobservice.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostDTOs.User;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.feignClient.UserClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class JobPostControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    @Autowired
    private JobPostRepository jobPostRepository;

    @MockitoBean
    private UserClient userClient; // Mock external service

    @BeforeEach
    void setUp() {
        jobPostRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void shouldCreateJobPostSuccessfully()  {

        String email = "recruiter@test.com";
        User mockRecruiter = new User(101L, "Recruiter", email, "RECRUITER");
        when(userClient.getUserIdByEmail(email)).thenReturn(mockRecruiter);

        JobPostRequest request = JobPostRequest.builder()
                .title("Senior Backend Developer")
                .companyName("Cloud Solutions")
                .description("Expertise in Java and Spring Boot")
                .location("Remote")
                .salary(95000.0)
                .build();


        mockMvc.perform(post("/jobs/post")
                        .header("X-User-Email", email)
                        .header("X-User-Role", "RECRUITER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Senior Backend Developer"))
                .andExpect(jsonPath("$.recruiterId").value(101L));
    }

    @SneakyThrows
    @Test
    void shouldThrow_Exception(){
        JobPostRequest request = new JobPostRequest("Dev", "Google", "Desc", "Remote", 500.0);

        mockMvc.perform(post("/jobs/post")
                    .header("X-User-Email" , "wrongroleemail@example.com")
                    .header("X-User-Role", "SEEKER")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User is not a recruiter."));
    }

    @SneakyThrows
    @Test
    void invalidInput_fromRecruiter(){
        JobPostRequest request = new JobPostRequest("Dev", "Google", "Desc", "Remote", -500.0);

        mockMvc.perform(post("/jobs/post")
                        .header("X-User-Email" , "wrongroleemail@example.com")
                        .header("X-User-Role", "RECRUITER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @SneakyThrows
    @Test
    void createJob_WhenUserServiceIsDown_ShouldReturnServiceUnavailable() {
        // Mocking user service returning null to simulate fallback or service down
        when(userClient.getUserIdByEmail(anyString())).thenReturn(null);

        JobPostRequest request = new JobPostRequest("Dev", "Google", "Desc", "Remote", 500.0);

        mockMvc.perform(post("/jobs/post")
                        .header("X-User-Email", "recruiter@test.com")
                        .header("X-User-Role", "RECRUITER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Unable to verify Recruiter identity. User Service is down."));
    }

    @SneakyThrows
    @Test
    void updateJob_WhenJobDoesNotExist_ShouldReturnInternalServerError(){
        JobPostRequest request = new JobPostRequest("Updated", "Google", "Desc", "Remote", 500.0);

        mockMvc.perform(put("/jobs/updatejob/999") // ID 999 does not exist
                        .header("X-User-Email", "recruiter@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    void updateJob_WhenUserIsNotOwner_ShouldReturnInternalServerError()  {
        // Create a job owned by recruiter 10
        JobPost existingJob = JobPost.builder()
                .title("Original")
                .recruiterId(10L)
                .build();
        JobPost savedJob = jobPostRepository.save(existingJob);

       //Another recruiter is trying to change the job post that is owned by another recruiter
        com.jobportal.jobservice.JobPostDTOs.User differentRecruiter =
                new com.jobportal.jobservice.JobPostDTOs.User(20L, "Other", "other@test.com", "RECRUITER");
        when(userClient.getUserIdByEmail("other@test.com")).thenReturn(differentRecruiter);
        //request by different recruiter
        JobPostRequest request = new JobPostRequest("Hacked", "Google", "Desc", "Remote", 500.0);

        mockMvc.perform(put("/jobs/updatejob/" + savedJob.getId())
                        .header("X-User-Email", "other@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getAllJobs_ShouldHitCacheOnSecondCall() {
        // This Request  Should hit the database
        mockMvc.perform(get("/public")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Verify repository was called once
        verify(jobPostRepository, times(1)).findAll(any(Pageable.class));

        // 2. Second Call: Should hit Redis and NOT the database
        mockMvc.perform(get("/public")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Verify repository still only called once total
        verify(jobPostRepository, times(1)).findAll(any(Pageable.class));
    }
}

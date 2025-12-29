package com.jobportal.jobservice.JobPostControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostController.JobPostController;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostDTOs.User;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import com.jobportal.jobservice.feignClient.JobApplicationClient;
import com.jobportal.jobservice.feignClient.UserClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(JobPostController.class)
@TestPropertySource(properties = "Server.port=8082")
public class JobPostControllersTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private  JobService jobService;
    @MockitoBean
    private  JobPostRepository jobPostRepository;
    @MockitoBean
    private  UserClient userClient;
    @MockitoBean
    private  JobApplicationClient jobApplicationClient;

    @SneakyThrows
    @Test
    void postJob_successfully() {
        String email = "recruiter@example.com";
        String role = "RECRUITER";

        JobPostRequest request = JobPostRequest.builder()
                .title("Java Developer")
                .companyName("Tech Corp")
                .description("Backend Role")
                .location("Remote")
                .salary(120000)
                .build();

        // This is the user object your service will return
        User mockRecruiter = User.builder()
                .id(1L)
                .email(email)
                .role(role)
                .build();
        // This is the job object your service will return after saving
        com.jobportal.jobservice.Entity.JobPost savedJob = com.jobportal.jobservice.Entity.JobPost.builder()
                .id(101L)
                .title("Java Developer")
                .recruiterId(1L)
                .build();


        // When controller calls getRecruiterByEmail, return our mockRecruiter
        when(jobService.getRecruiterByEmail(email)).thenReturn(mockRecruiter);

        // When controller calls createJobPost, return our savedJob
        when(jobService.createJobPost(any(JobPostRequest.class), eq(1L))).thenReturn(savedJob);


        mockMvc.perform(post("/jobs/post")
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)     // Required Heade
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convert obj to JSON
                .andExpect(status().isCreated()) // Expect 201
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.title").value("Java Developer"));

        log.info("Test Completed Successfully");
    }

    @SneakyThrows
    @Test
    void tryToCreateJob_whenNotARecruiter(){
        String email = "recruiter@example.com";
        String role = "SEEKER";

        JobPostRequest request = JobPostRequest.builder()
                .title("Java Developer")
                .companyName("Tech Corp")
                .description("Backend Role")
                .location("Remote")
                .salary(120000)
                .build();


        mockMvc.perform(post("/jobs/post")
                .header("X-User-Email" , email)
                .header("X-User-Role" , role)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }


    @SneakyThrows
    @Test
    void updateExistingJob_successfully(){

        String email = "recruiter@example.com";
        String role = "RECRUITER";
        User mockRecruiter = User.builder()
                .id(55L)
                .email(email)
                .role(role)
                .build();

        //1 .  updated job
        JobPost jobPost = JobPost.builder()
                .id(1L)
                .title("Python Developer")
                .companyName("Meta")
                .description("Backend Developer")
                .time(LocalDateTime.now())
                .salary(109090.0)
                .recruiterId(mockRecruiter.getId())
                .location("Remote")
                .applicationCount(0)
                .build();

        //2 . create a new job request for updating the old attributes
        JobPostRequest updateRequest = JobPostRequest.builder()
                .title("Python Developer")
                .companyName("Meta")
                .description("Backend Role")
                .location("Remote")
                .salary(120000)
                .build();
        when(userClient.getUserIdByEmail(email)).thenReturn(mockRecruiter);

        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);
        // This is the user object your service will return


        when(jobService.updateJob(1L , updateRequest , mockRecruiter.getId())).thenReturn(jobPost);

        mockMvc.perform(put("/jobs/updatejob/{jobId}" , 1L)
                .header("X-User-Email" , mockRecruiter.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Python Developer"))
                .andExpect(jsonPath("$.location").value("Remote"));
    }

    @SneakyThrows
    @Test
    void updateJob_Failure_JobNotFound() {
        Long nonExistentJobId = 999L;
        String email = "recruiter@example.com";

        User mockRecruiter = User.builder().id(55L).email(email).role("RECRUITER").build();
        JobPostRequest updateRequest = JobPostRequest.builder()
                .title("New Title")
                .companyName("Meta")
                .description("Desc")
                .location("Remote")
                .salary(100000)
                .build();


        when(userClient.getUserIdByEmail(email)).thenReturn(mockRecruiter);


        when(jobService.updateJob(eq(nonExistentJobId), any(JobPostRequest.class), eq(mockRecruiter.getId())))
                .thenThrow(new RuntimeException("Job not found"));

        // 3. Verify
        mockMvc.perform(put("/jobs/updatejob/{jobId}", nonExistentJobId)
                        .header("X-User-Email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError()) // Expected based on your ExceptionHandler
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @SneakyThrows
    @Test
    void deleteJob_successfully(){
        String email = "recruiter@example.com";
        Long recruiterId = 55L;
        String role = "RECRUITER";
        User mockRecruiter = User.builder()
                .id(55L)
                .email(email)
                .role(role)
                .build();
        JobPost jobToBeDeleted = JobPost.builder()
                .id(1L)
                .title("Java Dev")
                .description("Spring boot")
                .location("Remote")
                .companyName("Tech Corp")
                .applicationCount(0)
                .recruiterId(recruiterId)
                .build();

        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobToBeDeleted);

        when(userClient.getUserIdByEmail(email)).thenReturn(mockRecruiter);

        when(jobService.deleteJob(jobToBeDeleted.getId(), recruiterId)).thenReturn(true);

        mockMvc.perform(delete("/jobs/delete-job/{jobId}" , 1L)
                .header("X-User-Email" , mockRecruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().string("Job is successfully deleted"));

    }

    @SneakyThrows
    @Test
    void deleteJob_jobNotFound(){
        //In this test case 1L job Id does not exists
        //so this will throw run time exception
        String email = "recruiter@example.com";
        String role = "RECRUITER";

        User mockRecruiter = User.builder()
                .id(55L)
                .email(email)
                .role(role)
                .build();
        when(userClient.getUserIdByEmail(email)).thenReturn(mockRecruiter);

        doThrow(new RuntimeException("Job not found"))
                .when(jobService).deleteJob(1L , mockRecruiter.getId());

        mockMvc.perform(delete("/jobs/delete-job/{jobId}", 1L)
                        .header("X-User-Email", email))
                .andExpect(status().isInternalServerError()) // Default response from Exception class
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}

package com.jobportal.ApplicationService.JobApplicationControllerTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationController.JobApplicationController;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;
import com.jobportal.ApplicationService.JobApplicationService.KafkaProducerService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "server.port=8083")
public class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private   JobApplicationService jobApplicationService;

    @MockitoBean
    private   UserClient userClient;

    @MockitoBean
    private  KafkaProducerService kafkaProducerService;

    @MockitoBean
    private  JobPostClient jobPostClient;

    @MockitoBean
    private  JobApplicationRepository jobApplicationRepository;

//    @Test
//    void applyToJobAsync() throws Exception{
//
//        JobApplicationDto applicationRequest = new JobApplicationDto("lokesh's_resume");
//        when(jobApplicationService.getSeekerIdByEmail("lokesh@gmail.com")).thenReturn(1L);
//        when(jobApplicationService.applyToJobAsync(any() , any() , any()))
//                .thenReturn(CompletableFuture.completedFuture(null));
//
//        mockMvc.perform(post("/job-applications/apply-to/{jobId}", 1L)
//                        .header("X-User-Email", "lokesh@gmail.com")
//                        .header("X-User-Role", "SEEKER")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(applicationRequest)))
//                .andExpect(status().isAccepted())
//                .andExpect(content().string("Application Submitted Successfully"));
//
//
//    }
//
//    @Test
//    void apply_Forbidden_WhenUserIsNotSeeker() throws Exception {
//        JobApplicationDto dto = new JobApplicationDto("resume.pdf");
//
//        mockMvc.perform(post("/job-applications/apply-to/{jobId}", 55L)
//                        .header("X-User-Email", "recruiter@example.com")
//                        .header("X-User-Role", "RECRUITER") // Wrong Role
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isForbidden())
//                .andExpect(content().string("User is not Seeker"));
//
//        // Ensure service logic was skipped
//        verify(jobApplicationService, never()).getSeekerIdByEmail(any());
//    }
//
//    @Test
//    void apply_ServiceUnavailable_WhenUserServiceDown() throws Exception {
//        Long jobId = 55L;
//        String email = "seeker@example.com";
//        JobApplicationDto dto = new JobApplicationDto("resume.pdf");
//        when(jobApplicationService.getSeekerIdByEmail(email)).thenReturn(null); // here fallBack method will be called because user service is down
//
//
//        mockMvc.perform(post("/job-applications/apply-to/{jobId}", jobId)
//                        .header("X-User-Email", email)
//                        .header("X-User-Role", "SEEKER")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isServiceUnavailable())
//                .andExpect(content().string("Cannot apply. User Service is currently unavailable."));
//    }

}

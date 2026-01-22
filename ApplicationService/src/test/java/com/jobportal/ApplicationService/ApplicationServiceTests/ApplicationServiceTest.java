package com.jobportal.ApplicationService.ApplicationServiceTests;


import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.FeignClient.UserClient;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.JobApplicationService.FileStorageService;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;


import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    private JobApplicationService jobApplicationService;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserClient userClient;

    @Mock
    private JobPostClient jobPostClient;

    @Mock
    private Executor executor;



}

package com.jobportal.ApplicationService.ApplicationServiceTests;

import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;
import com.jobportal.ApplicationService.enums.EventStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    private JobApplicationService jobApplicationService;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Test
    void applyToJobAsync(){

        JobApplicationDto application = new JobApplicationDto("lokesh's_resume.pdf");

        when(jobApplicationRepository.existsBySeekerIdAndJobPostId(1L , 55L)).thenReturn(false);
        JobApplication jobApplication = JobApplication.builder()
                .applicationId(100L)
                .resumeUrl(application.getResumeUrl())
                .seekerId(1L)
                .jobPostId(55L)
                .appliedAt(LocalDateTime.now())
                .build();

        jobApplicationRepository.save(any(JobApplication.class));
        outboxEventRepository.save(any(Events.class));

        CompletableFuture<Void> future = jobApplicationService.applyToJobAsync(1L , application , 55L);

        future.join(); //wait for operation to finish

        ArgumentCaptor<JobApplication> jobApplicationArgumentCaptor = ArgumentCaptor.forClass(JobApplication.class);
        verify(jobApplicationRepository , times(2)).save(jobApplicationArgumentCaptor.capture());

        JobApplication capturedJob = jobApplicationArgumentCaptor.getValue();

        assertEquals(1L, capturedJob.getSeekerId());
        assertEquals(55L, capturedJob.getJobPostId());
        assertEquals("lokesh's_resume.pdf", capturedJob.getResumeUrl());


        ArgumentCaptor<Events> eventsArgumentCaptor = ArgumentCaptor.forClass(Events.class);
        verify(outboxEventRepository , times(2)).save(eventsArgumentCaptor.capture());

        Events capturedEvent = eventsArgumentCaptor.getValue();

        assertEquals("job-application-events", capturedEvent.getTopic());
        assertEquals(String.valueOf(55L), capturedEvent.getPayload());
        assertEquals(EventStatus.PENDING, capturedEvent.getStatus());


    }

    @Test
    void applyToJobAsync_ThrowsException_AlreadyApplied(){
        JobApplicationDto application = new JobApplicationDto("lokesh's_resume.pdf");
        when(jobApplicationRepository.existsBySeekerIdAndJobPostId(1L , 55L)).thenReturn(true);
        Exception exception =  assertThrows(IllegalStateException.class ,
                                () -> jobApplicationService.applyToJobAsync(1L ,application , 55L ));

        assertEquals("You have already applied to this job." , exception.getMessage());

    }

    @Test
    void getSeekerIdFallbackTest_shouldReturnNull(){
        String email = "lokesh@email.com";

        Long res = jobApplicationService.getSeekerIdFallback(email);

        assertNull(res);
    }

}

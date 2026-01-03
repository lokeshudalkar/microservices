package com.jobportal.ApplicationService.IntgrationTest;


import com.jobportal.ApplicationService.Entity.Events;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import com.jobportal.ApplicationService.JobApplicationRepository.OutboxEventRepository;
import com.jobportal.ApplicationService.JobApplicationService.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class IntegrationTests extends AbstractIntegrationTest{

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @MockitoBean
    private OutboxEventRepository outboxEventRepository;



//    @Test
//    void shouldRollbackApplication_WhenOutboxSaveFails() {
//
//        Long seekerId = 123L;
//        Long jobId = 456L;
//        JobApplicationDto dto = new JobApplicationDto("https://resume.com/lokesh's_resume.pdf");
//
//        // Force the Outbox save to fail with a RuntimeException
//        // a database error specifically for the events table
//        when(outboxEventRepository.save(any(Events.class)))
//                .thenThrow(new RuntimeException("Outbox Failure"));
//
//        //Call the async method
//        CompletableFuture<Void> future = jobApplicationService.applyToJobAsync(seekerId, dto, jobId);
//
//        // Wait for the async thread to finish and capture the exception
//        future.handle((res, ex) -> {
//            assertNotNull(ex, "The async execution should have thrown an exception");
//            return null;
//        }).join(); // Block the test thread until the virtual thread finishes its rollback
//
//        //  Verify that the JobApplication was NOT saved (rolled back)
//        boolean exists = jobApplicationRepository.existsBySeekerIdAndJobPostId(seekerId, jobId);
//        assertFalse(exists, "Transactional failure: JobApplication was not rolled back when Outbox failed!");
//    }
}

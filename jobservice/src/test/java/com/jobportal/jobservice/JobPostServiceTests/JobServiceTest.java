package com.jobportal.jobservice.JobPostServiceTests;

import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import com.jobportal.jobservice.feignClient.JobApplicationClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobPostRepository jobPostRepository;

    @InjectMocks
    private JobService jobService;

    @Mock
    private JobApplicationClient jobApplicationClient;

    @Test
    void createJobPostTest(){
        JobPostRequest request = new JobPostRequest("Java Dev", "Google", "Desc", "Remote", 1000.0);
        JobPost expectedJob = JobPost.builder().id(1L).title("Java Dev").build();

        when(jobPostRepository.save(any(JobPost.class))).thenReturn(expectedJob);

        JobPost res = jobService.createJobPost(request , 55L);

        assertNotNull(res);
        assertEquals("Java Dev" , res.getTitle());

        verify(jobPostRepository, times(1)).save(any(JobPost.class));

    }

    @Test
    void updateJobPost(){

        JobPostRequest request = new JobPostRequest("Java Dev", "Google", "Desc", "Remote", 1000.0);
        JobPost existingJob = JobPost.builder().id(1L).title("Java Dev").recruiterId(55L).build();
        JobPost updatedJob = JobPost.builder().id(1L).title("Python Dev").recruiterId(55L).build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(existingJob));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(updatedJob);

        JobPost res = jobService.updateJob(1L , request , 55L);
        assertNotNull(res);
        assertEquals("Python Dev" , res.getTitle());

        verify(jobPostRepository, times(1)).findById(1L);
        verify(jobPostRepository , times(1)).save(any(JobPost.class));
    }

    @Test
    void updateJobWhenUserIsUnauthorised(){
        JobPostRequest request = new JobPostRequest("Java Dev", "Google", "Desc", "Remote", 1000.0);

        JobPost existingJob = JobPost.builder().id(1L).title("Python Dev").recruiterId(55L).build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(existingJob));

        RuntimeException exception = assertThrows(RuntimeException.class , () -> jobService.updateJob(1L , request , 56L));

        assertEquals("Not authorized to update this job post.", exception.getMessage());

        verify(jobPostRepository, times(0)).save(any(JobPost.class));

    }

    @Test
    void updateJobPostThrowException(){ // when job id is not found in database
        JobPostRequest request = new JobPostRequest("Java Dev", "Google", "Desc", "Remote", 1000.0);
        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class , () -> jobService.updateJob(1L , request , 55L));

        verify(jobPostRepository , times(0)).save(any(JobPost.class));

    }
    @Test
    void deleteJob(){
        JobPost existingJob = JobPost.builder().id(1L).title("Java Dev").recruiterId(55L).build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(existingJob));


        assertTrue(jobService.deleteJob(1L,55L));

        verify(jobApplicationClient, times(1)).deleteJobApplicationOfJobPost(1L); // Check external call
        verify(jobPostRepository, times(1)).delete(existingJob);
    }

    @Test
    void deleteJobThrowsException(){
        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class , () -> jobService.deleteJob(1L , 55L));
        verify(jobApplicationClient , times(0)).deleteJobApplicationOfJobPost(1L);
        verify(jobPostRepository , times(0)).delete(any(JobPost.class));
    }

    @Test
    void deleteJobWhenUserIsNotOwner(){
        JobPost existingJob = JobPost.builder().id(1L).title("Java Dev").recruiterId(55L).time(LocalDateTime.now()).build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(existingJob));
        RuntimeException exception = assertThrows(RuntimeException.class , () -> jobService.deleteJob(1L , 56L));
        assertEquals("Not authorized to delete this job!" , exception.getMessage());
        verify(jobPostRepository , times(1)).findById(1L);
        verify(jobApplicationClient , times(0)).deleteJobApplicationOfJobPost(1L);
        verify(jobPostRepository , times(0)).delete(any(JobPost.class));
    }

    @Test
    void incrementApplicationCount(){
        JobPost existingJob = JobPost.builder().id(1L).title("Java Dev").recruiterId(55L).time(LocalDateTime.now()).applicationCount(0).build();

        JobPost updatedJob = JobPost.builder().id(1L).title("Python Dev").recruiterId(55L).applicationCount(1).build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(existingJob));

        when(jobPostRepository.save(any(JobPost.class))).thenReturn(updatedJob);

        JobPost res = jobService.incrementApplicationCount(1L);

        assertEquals(1 , res.getApplicationCount());

        verify(jobPostRepository , times(1)).save(existingJob);
    }

    @Test
    void getJobApplications_fallback_method(){
        var result = jobService.getJobApplicationsFallback(1L , new RuntimeException("server is down"));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRecruiterFallback_shouldReturnNull() {
        String email = "test@example.com";
        Throwable dummyException = new RuntimeException("User Service Timeout");

        var result = jobService.getRecruiterFallback(email, dummyException);

        assertNull(result);
    }
}


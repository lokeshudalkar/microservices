package com.jobportal.jobservice.JobPostControllerTest;



import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostController.JobPostController;
import com.jobportal.jobservice.JobPostController.PublicController;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostDTOs.User;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import com.jobportal.jobservice.feignClient.UserClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import java.time.LocalDateTime;


@ExtendWith(MockitoExtension.class)
class JobPostControllerTest {



    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private JobService jobService;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private JobPostController jobPostController;

    @InjectMocks
    private PublicController publicController;

    @Test
    void CreatejobTest(){
        User  mockRecruiter = new User(101L, "Test Recruiter", "recruiter@example.com", "RECRUITER");
        JobPostRequest jobRequest = new JobPostRequest("Senior Java Developer", "Innovatech", "A role...", "Remote", 150000.0);
        JobPost expectedJob = new JobPost(1L, "Senior Java Developer", "Innovatech", "A role...", "Remote", 150000.0,
                LocalDateTime.now(), 101L);

        when(userClient.getUserByEmail("recruiter@example.com")).thenReturn(mockRecruiter);
        when(jobService.createJobPost(any(JobPostRequest.class) , eq(101L))).thenReturn(expectedJob);

        ResponseEntity<?> responseEntity = jobPostController.createJob("recruiter@example.com", "RECRUITER",
                jobRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isInstanceOf(JobPost.class);

        JobPost actualPost = (JobPost) responseEntity.getBody();

        assertThat(actualPost.getTitle()).isEqualTo("Senior Java Developer");
        assertThat(actualPost.getRecruiterId()).isEqualTo(101L);

    }

    @Test
    void updateJobTest(){
        User  mockRecruiter = new User(101L, "Test Recruiter", "recruiter@example.com", "RECRUITER");
        JobPostRequest jobRequest = new JobPostRequest("Senior Java Developer", "Innovatech", "A role...", "Remote", 150000.0);
        JobPost expectedJob = new JobPost(1L, "Senior Java Developer", "Nova-tech", "A role...", "Remote", 150000.0,
                LocalDateTime.now(), 101L);
        when(userClient.getUserByEmail("recruiter@example.com")).thenReturn(mockRecruiter);
        when(jobService.updateJob(eq(1L), any(JobPostRequest.class), eq(101L))).thenReturn(expectedJob);

        ResponseEntity<?> responseEntity = jobPostController.updateJob(jobRequest , "recruiter@example.com",
                1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isInstanceOf(JobPost.class);

        JobPost actualPost = (JobPost) responseEntity.getBody();

        assertThat(actualPost.getTitle()).isEqualTo("Senior Java Developer");
        assertThat(actualPost.getRecruiterId()).isEqualTo(101L);

    }


    @Test
    void deleteJobTest(){
        User  mockRecruiter = new User(101L, "Test Recruiter", "recruiter@example.com", "RECRUITER");
        JobPost jobToBEDeleted = new JobPost(1L, "Senior Java Developer", "Nova-tech", "A role...", "Remote", 150000.0,
                LocalDateTime.now(), 101L);
        when(userClient.getUserByEmail("recruiter@example.com")).thenReturn(mockRecruiter);
        when(jobService.deleteJob(eq(1L) , eq(101L))).thenReturn(true);
        
        ResponseEntity<?> responseEntity = jobPostController.deleteJob("recruiter@example.com" ,1L );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Job is successfully deleted");

    }

//    @Test
//    void deleteJobWhenRIdIsNull() {
//        JobPost jobToBEDeleted = new JobPost(1L, "Senior Java Developer", "Nova-tech", "A role...", "Remote", 150000.0,
//                LocalDateTime.now(), 101L);
//        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobToBEDeleted));
//        assertThatThrownBy(() -> jobService.deleteJob(1L ,  999L))
//                .isInstanceOf(Exception.class)
//                .hasMessage("Not authorized to delete this job");
//        verify(jobPostRepository, never()).delete(any());
//    }

    @Test
    void getJobsByKeyword(){

        JobPostRequest jobRequest = new JobPostRequest("Senior Java Developer", "Innovatech", "A role...", "Remote", 150000.0);
        JobPost expectedJob = new JobPost(1L, "Senior Java Developer", "Nova-tech", "A role...", "Remote", 150000.0 ,LocalDateTime.now()
                , 101L);

        JobPost expectedJob1 = new JobPost(2L, "Senior python Developer", "Nova-tech", "A role...", "Remote", 150000.0,LocalDateTime.now()
               , 101L);
        jobPostRepository.save(expectedJob);
        jobPostRepository.save(expectedJob1);
        ResponseEntity<?> responseEntity = publicController.searchJobsByKeyword("java");
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedJob1);

    }
}

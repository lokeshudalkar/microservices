package com.jobportal.jobservice.JobPostController;

import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobApplication;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostDTOs.User;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.JobServices.JobService;
import com.jobportal.jobservice.feignClient.JobApplicationClient;
import com.jobportal.jobservice.feignClient.UserClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * The type Job post controller.
 */
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobPostController {

    private final JobService jobService;
    private final JobPostRepository jobPostRepository;
    private final UserClient userClient;
    private final JobApplicationClient jobApplicationClient;

    /**
     * Create job response entity.
     *
     * @param email          the email
     * @param role           the role
     * @param jobPostRequest the job post request
     * @return the response entity
     */
    @PostMapping("/post")
    public ResponseEntity<?> createJob(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody JobPostRequest jobPostRequest) {

        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter.");
        }

//        User recruiter = userClient.getUserIdByEmail(email);
        User recruiter = jobService.getRecruiterByEmail(email);

        if (recruiter == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Unable to verify Recruiter identity. User Service is down.");
        }

        JobPost jobPost = jobService.createJobPost(jobPostRequest, recruiter.getId());
        return new ResponseEntity<>(jobPost, HttpStatus.CREATED);
    }


    /**
     * Update job response entity.
     *
     * @param jobPostRequest the job post request
     * @param email          the email
     * @param jobId          the job id
     * @return the response entity
     */
    @PutMapping("/updatejob/{jobId}")
    public ResponseEntity<?> updateJob(@RequestBody JobPostRequest jobPostRequest,
                                       @RequestHeader("X-User-Email") String email,
                                       @PathVariable Long jobId) {

        String recruiterEmail = email;

        User recruiter = userClient.getUserIdByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }

        JobPost jobPost = jobService.updateJob(jobId, jobPostRequest, recruiter.getId());
        return ResponseEntity.ok(jobPost);
    }

    /**
     * Delete job response entity.
     *
     * @param email the email
     * @param jobId the job id
     * @return the response entity
     */
    @DeleteMapping("/delete-job/{jobId}")
    public ResponseEntity<?> deleteJob(@RequestHeader("X-User-Email") String email,
                                       @PathVariable Long jobId) {

        String recruiterEmail = email;

        User recruiter = userClient.getUserIdByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        jobService.deleteJob(jobId, recruiter.getId());
        return ResponseEntity.ok().body("Job is successfully deleted");

    }

    /**
     * Gets all job posted by recruiter.
     *
     * @param email the email
     * @return the all job posted by recruiter
     */
    @GetMapping("/my-jobs")
    public ResponseEntity<?> getAllJobPostedByRecruiter(@RequestHeader("X-User-Email") String email) {

        String recruiterEmail = email;

        User recruiter = userClient.getUserIdByEmail(recruiterEmail);
        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a recruiter or does not exist.");
        }
        return ResponseEntity.ok(jobPostRepository.findByRecruiterId(recruiter.getId()));
    }

    /**
     * Gets job applications.
     *
     * @param email the email
     * @param jobId the job id
     * @return the job applications
     */
    @GetMapping("/{jobId}/applications")
    public ResponseEntity<?> getJobApplications(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long jobId
    ) {

        String recruiterEmail = email;
        User recruiter = userClient.getUserIdByEmail(recruiterEmail);

        if (recruiter == null || !"RECRUITER".equals(recruiter.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only recruiters can view job applications.");
        }

        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));


        if (!jobPost.getRecruiterId().equals(recruiter.getId())) { //
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view applications for this job.");
        }


//        Page<JobApplication> applications = jobApplicationClient.getApplicationsForJob(jobId);
        List<JobApplication> applications = jobService.getApplicationsForJob(jobId);

        return ResponseEntity.ok(applications);
    }
}

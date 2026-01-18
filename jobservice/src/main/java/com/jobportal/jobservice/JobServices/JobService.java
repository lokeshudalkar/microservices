package com.jobportal.jobservice.JobServices;


import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobApplication;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostDTOs.User;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import com.jobportal.jobservice.feignClient.JobApplicationClient;
import com.jobportal.jobservice.feignClient.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * The type Job service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final UserClient userClient;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationClient jobApplicationClient;


    /**
     * Create job post.
     *
     * @param jobPostRequest the job post request
     * @param recruiterId    the recruiter id
     * @return the job post
     */
    @Transactional
    @CacheEvict(value = {"allJobs", "jobsearch"}, allEntries = true)
    public JobPost createJobPost(JobPostRequest jobPostRequest, Long recruiterId) {
        JobPost jobPost = JobPost.builder()
                .title(jobPostRequest.getTitle())
                .companyName(jobPostRequest.getCompanyName())
                .description(jobPostRequest.getDescription())
                .location(jobPostRequest.getLocation())
                .salary(jobPostRequest.getSalary())
                .recruiterId(recruiterId)
                .time(LocalDateTime.now())
                .build();

        return jobPostRepository.save(jobPost);
    }

    /**
     * Gets all job post.
     *
     * @param recruiterId the recruiter id
     * @return the all job post
     */
    public List<JobPost> getAllJobPost(Long recruiterId) {
        return jobPostRepository.findByRecruiterId(recruiterId);
    }

    /**
     * Update job post.
     *
     * @param jobId          the job id
     * @param jobPostRequest the job post request
     * @param recruiterId    the recruiter id
     * @return the job post
     */
    @Transactional
    @CacheEvict(value = {"allJobs", "jobsearch"}, allEntries = true)
    public JobPost updateJob(Long jobId, JobPostRequest jobPostRequest, Long recruiterId) {
        JobPost jobPost = jobPostRepository.findById(jobId).orElseThrow(
                () -> new RuntimeException("Job not found")
        );

        if (!jobPost.getRecruiterId().equals(recruiterId)) {

            // If the ID from the token does not match the ID on the job post, deny access.
            throw new RuntimeException("Not authorized to update this job post.");

        }

        jobPost.setTitle(jobPostRequest.getTitle());
        jobPost.setCompanyName(jobPostRequest.getCompanyName());
        jobPost.setDescription(jobPostRequest.getDescription());
        jobPost.setLocation(jobPostRequest.getLocation());
        jobPost.setSalary(jobPostRequest.getSalary());
        jobPost.setTime(LocalDateTime.now());

        return jobPostRepository.save(jobPost);
    }

    /**
     * Delete job boolean.
     *
     * @param jobId       the job id
     * @param recruiterId the recruiter id
     * @return the boolean
     */
    @Transactional
    @CacheEvict(value = {"allJobs", "jobsearch"}, allEntries = true)
    @CircuitBreaker(name = "applicationServiceBreaker", fallbackMethod = "deleteJobFallback")
    public boolean deleteJob(Long jobId, Long recruiterId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!jobPost.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to delete this job!");
        }

        jobApplicationClient.deleteJobApplicationOfJobPost(jobId);
        jobPostRepository.delete(jobPost);
        return true;
    }

    /**
     * Delete job fallback boolean.
     *
     * @param jobId       the job id
     * @param recruiterId the recruiter id
     * @param t           the t
     * @return the boolean
     */
    public boolean deleteJobFallback(Long jobId, Long recruiterId, Throwable t) {
        log.error("Application Service is down. Cannot delete job applications. Aborting deletion. Error: {}", t.getMessage());
        return false;
    }

    /**
     * Gets all jobs.
     *
     * @return the all jobs
     */
    public List<JobPost> getAllJobs() {
        return jobPostRepository.findAll();
    }


    /**
     * Increment application count job post.
     *
     * @param jobId the job id
     * @return the job post
     */
    @Transactional
    public JobPost incrementApplicationCount(Long jobId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found for ID: " + jobId));

        jobPost.setApplicationCount(jobPost.getApplicationCount() + 1);
        return jobPostRepository.save(jobPost);
    }


    /**
     * Gets recruiter by email.
     *
     * @param email the email
     * @return the recruiter by email
     */
    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "getRecruiterFallback")
    public User getRecruiterByEmail(String email) {
        return userClient.getUserIdByEmail(email);
    }

    /**
     * Gets recruiter fallback.
     *
     * @param email the email
     * @param t     the t
     * @return the recruiter fallback
     */
//  the fallback method
    public User getRecruiterFallback(String email, Throwable t) {
        log.error("User Service is down, cannot fetch Recruiter ID: {}", t.getMessage());
        return null;
    }


    /**
     * Gets applications for job.
     *
     * @param jobId the job id
     * @return the applications for job
     */
    @CircuitBreaker(name = "applicationServiceBreaker", fallbackMethod = "getJobApplicationsFallback")
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        return jobApplicationClient.getApplicationsForJob(jobId);
    }

    /**
     * Gets job applications fallback.
     *
     * @param jobId the job id
     * @param t     the t
     * @return the job applications fallback
     */
    public List<JobApplication> getJobApplicationsFallback(Long jobId, Throwable t) {//do we need params in fall back methods
        log.error("Application Service is down. Returning empty application list. Error: {}", t.getMessage());
        return List.of();
    }


}

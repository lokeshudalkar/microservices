package com.jobportal.jobservice.JobServices;


import com.jobportal.jobservice.Entity.JobPost;
import com.jobportal.jobservice.JobPostDTOs.JobPostRequest;
import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService  {

    private final JobPostRepository jobPostRepository;

    @Transactional
    public JobPost createJobPost(JobPostRequest jobPostRequest , Long recruiterId){
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

    public List<JobPost> getAllJobPost(Long recruiterId){
        return jobPostRepository.findByRecruiterId(recruiterId);
    }

    @Transactional
    public JobPost updateJob(Long jobId , JobPostRequest jobPostRequest , Long recruiterId){
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
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

    @Transactional
    public boolean deleteJob(Long jobId, Long recruiterId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!jobPost.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to delete this job!");
        }

        jobPostRepository.delete(jobPost);
        return true;
    }

    public List<JobPost> getAlljobs(){
        return jobPostRepository.findAll();
    }

    @Transactional
    public JobPost incrementApplicationCount(Long jobId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found for ID: " + jobId));

        jobPost.setApplicationCount(jobPost.getApplicationCount() + 1);
        return jobPostRepository.save(jobPost);
    }

}

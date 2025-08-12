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
public class JobService {

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

    public List<JobPost> getAllJobPost(){
        return jobPostRepository.findAll();
    }

    @Transactional
    public JobPost updateJob(Long jobId , JobPostRequest jobPostRequest , Long recruiterId){
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if(jobPost.getRecruiterId().equals(recruiterId)){
            throw new RuntimeException("not allowed to update this job");
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
    public void deleteJob(Long jobId, Long recruiterId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!jobPost.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to delete this job");
        }

        jobPostRepository.delete(jobPost);
    }


}

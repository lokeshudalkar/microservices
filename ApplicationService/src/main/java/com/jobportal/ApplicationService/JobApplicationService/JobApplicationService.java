package com.jobportal.ApplicationService.JobApplicationService;


import com.jobportal.ApplicationService.Entity.JobApplication;
import com.jobportal.ApplicationService.FeignClient.JobPostClient;
import com.jobportal.ApplicationService.JobApplicationDto.JobApplicationDto;
import com.jobportal.ApplicationService.JobApplicationRepository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
//    private final JobPostClient jobPostClient;


    @Transactional
    public void applyToJob(Long seekerId , JobApplicationDto jobApplicationDto , Long jobId ){

        //check if link is empty or not
        if(!StringUtils.hasText(jobApplicationDto.getResumeUrl())){
            throw new IllegalStateException("Resume url cannot be empty");
        }

        boolean alreadyApplied = jobApplicationRepository
        .existsBySeekerIdAndJobPostId(seekerId, jobId);

    if (alreadyApplied) {
        throw new IllegalStateException("You have already applied to this job.");
    }

        JobApplication jobApplication = JobApplication.builder()
        .resumeUrl(jobApplicationDto.getResumeUrl())
        .appliedAt(LocalDateTime.now())
        .jobPostId(jobId)
        .seekerId(seekerId)
        .build();
       jobApplicationRepository.save(jobApplication);

    }
}

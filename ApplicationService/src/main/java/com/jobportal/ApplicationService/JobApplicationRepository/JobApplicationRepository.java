package com.jobportal.ApplicationService.JobApplicationRepository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jobportal.ApplicationService.Entity.JobApplication;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication , Long> {
    List<JobApplication> findByApplicationId(Long ApplicationId);
    boolean existsBySeekerIdAndJobPostId(Long seekerId, Long jobPostId);
    List<JobApplication> findByJobPostId(Long jobId);

    @Modifying
    @Transactional
    void deleteByJobPostId(Long jobId);
}

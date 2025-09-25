package com.jobportal.ApplicationService.JobApplicationRepository;


import com.jobportal.ApplicationService.Entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication , Long> {
    List<JobApplication> findByApplicationId(Long ApplicationId);
    boolean existsBySeekerIdAndJobPostId(Long seekerId, Long jobPostId);

}

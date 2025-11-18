package com.jobportal.jobservice.JobPostRepository;


import com.jobportal.jobservice.Entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByRecruiterId(Long recruiterId);
    Page<JobPost> findByTitleContainingOrDescriptionContainingOrLocationContainingAllIgnoreCase(
        String title, String description, String location , Pageable pageable
    );

    boolean existsByTitleAndCompanyNameAndRecruiterId(String title, String companyName, Long recruiterId);
}

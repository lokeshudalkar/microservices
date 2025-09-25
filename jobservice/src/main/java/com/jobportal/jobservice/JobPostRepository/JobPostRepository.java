package com.jobportal.jobservice.JobPostRepository;


import com.jobportal.jobservice.Entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByRecruiterId(Long recruiterId);
    List<JobPost> findByTitleContainingOrDescriptionContainingOrLocationContainingAllIgnoreCase(
        String title, String description, String location
    );
}

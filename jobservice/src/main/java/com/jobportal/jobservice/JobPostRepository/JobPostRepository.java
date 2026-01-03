package com.jobportal.jobservice.JobPostRepository;


import com.jobportal.jobservice.Entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The interface Job post repository.
 */
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    /**
     * Find by recruiter id list.
     *
     * @param recruiterId the recruiter id
     * @return the list
     */
    List<JobPost> findByRecruiterId(Long recruiterId);

    /**
     * Find by title containing or description containing or location containing all ignore case page.
     *
     * @param title       the title
     * @param description the description
     * @param location    the location
     * @param pageable    the pageable
     * @return the page
     */
    Page<JobPost> findByTitleContainingOrDescriptionContainingOrLocationContainingAllIgnoreCase(
            String title, String description, String location, Pageable pageable
    );

    /**
     * Exists by title and company name and recruiter id boolean.
     *
     * @param title       the title
     * @param companyName the company name
     * @param recruiterId the recruiter id
     * @return the boolean
     */
    boolean existsByTitleAndCompanyNameAndRecruiterId(String title, String companyName, Long recruiterId);
}

package com.jobportal.jobservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * The type Job post.
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_post", indexes = {
        @Index(name = "idx_recruiter_id", columnList = "recruiterId"),
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_location", columnList = "location")
})
public class JobPost {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String companyName;

    private String description;

    private String location;

    private double salary;

    private LocalDateTime time;

    private Long recruiterId; // it will come from user-service

    private int applicationCount = 0;


}

package com.jobportal.jobservice.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_post")
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
    private Long recruiterId; //it will come from user_db;

//    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
//    private List<JobApplication> applications;

}

package com.jobportal.jobservice.JobPostDTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostRequest {
    private String title;
    private String companyName;
    private String description;
    private String location;
    private double salary;
}

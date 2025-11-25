package com.jobportal.jobservice.JobPostDTOs;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostRequest {


    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company Name is required")
    private String companyName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 0, message = "Salary cannot be negative")
    private double salary;
}

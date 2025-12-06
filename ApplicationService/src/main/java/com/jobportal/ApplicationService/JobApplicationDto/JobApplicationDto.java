package com.jobportal.ApplicationService.JobApplicationDto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationDto{

    @NotBlank(message = "Resume URL is required for applying to jobs")
    private String resumeUrl;
}

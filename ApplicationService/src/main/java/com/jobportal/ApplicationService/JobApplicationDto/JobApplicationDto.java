package com.jobportal.ApplicationService.JobApplicationDto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationDto{

    @NotBlank(message = "Resume URL is required for applying to jobs")
    private MultipartFile resumeUrl;
}

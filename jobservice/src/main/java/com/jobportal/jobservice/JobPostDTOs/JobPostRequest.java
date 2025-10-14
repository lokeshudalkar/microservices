package com.jobportal.jobservice.JobPostDTOs;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostRequest {

    @NotNull
    private String title;

    @NotNull
    private String companyName;

    @NotNull
    private String description;

    @NotNull
    private String location;

    private double salary;
}

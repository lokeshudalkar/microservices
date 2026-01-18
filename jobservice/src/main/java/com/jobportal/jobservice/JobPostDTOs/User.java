package com.jobportal.jobservice.JobPostDTOs;

import lombok.*;

/**
 * The type User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    private String name;

    private String email;

    private String role;
}
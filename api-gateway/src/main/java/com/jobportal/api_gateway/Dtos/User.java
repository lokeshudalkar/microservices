package com.jobportal.api_gateway.Dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;

    private String name;

    private String email;

    private String role;

    public boolean hasRole(String role) {
        return this.role != null && this.role.equalsIgnoreCase(role);
    }

    /**
     * Check if user is a recruiter
     * @return true if user is a recruiter
     */
    public boolean isRecruiter() {
        return hasRole("RECRUITER");
    }

    /**
     * Check if user is a job seeker
     * @return true if user is a job seeker
     */
    public boolean isSeeker() {
        return hasRole("SEEKER");
    }

    /**
     * Check if user is an admin
     * @return true if user is an admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}

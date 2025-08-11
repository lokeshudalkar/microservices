package com.jobportal.user_service.UserDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @jakarta.validation.constraints.NotBlank
    private String email;
    @NotBlank
    private String password;
}

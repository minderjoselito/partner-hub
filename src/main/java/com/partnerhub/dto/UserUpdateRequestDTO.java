package com.partnerhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequestDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String name;
}

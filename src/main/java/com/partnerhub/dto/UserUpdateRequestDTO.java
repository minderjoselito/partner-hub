package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload to update a user's profile information.
 */
@Schema(description = "Payload to update a user's profile information.")
public class UserUpdateRequestDTO {

    @NotBlank
    @Email
    @Schema(description = "Updated email address", example = "new.email@example.com")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Updated full name", example = "New Name")
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

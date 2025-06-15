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

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Schema(
            description = "Updated email address",
            example = "new.email@example.com",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 120, message = "Name must be at most 120 characters")
    @Schema(
            description = "Updated full name",
            example = "New Name",
            maxLength = 120,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
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

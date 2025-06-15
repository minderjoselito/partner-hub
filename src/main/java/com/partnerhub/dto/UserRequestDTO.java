package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload to create or register a new user.
 */
@Schema(description = "Payload to create or register a new user.")
public class UserRequestDTO {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Schema(
            description = "User's unique email address",
            example = "john.doe@example.com",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Schema(
            description = "User's password (8 to 64 characters)",
            example = "MySecurePass123",
            minLength = 8,
            maxLength = 64,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @Size(max = 120, message = "Name must be at most 120 characters")
    @Schema(
            description = "Full name of the user",
            example = "John Doe",
            maxLength = 120
    )
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

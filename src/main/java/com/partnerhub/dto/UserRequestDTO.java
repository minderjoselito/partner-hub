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

    @NotBlank
    @Email
    @Schema(description = "User's unique email address", example = "john.doe@example.com", maxLength = 200)
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    @Schema(description = "User's password (8 to 64 characters)", example = "MySecurePass123")
    private String password;

    @Size(max = 120)
    @Schema(description = "Full name of the user", example = "John Doe")
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

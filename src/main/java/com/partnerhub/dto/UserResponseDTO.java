package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Response payload representing a user.
 */
@Schema(description = "Response payload representing a user.")
public class UserResponseDTO {

    @Schema(
            description = "Unique identifier of the user",
            example = "42",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
            description = "Email address of the user",
            example = "john.doe@example.com",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
            description = "Full name of the user",
            example = "John Doe",
            maxLength = 120
    )
    private String name;

    @Schema(
            description = "Date and time the user was created",
            example = "2024-01-15T10:00:00Z",
            type = "string",
            format = "date-time"
    )
    private Instant createdAt;

    @Schema(
            description = "Date and time the user was last updated",
            example = "2024-06-14T14:32:00Z",
            type = "string",
            format = "date-time"
    )
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

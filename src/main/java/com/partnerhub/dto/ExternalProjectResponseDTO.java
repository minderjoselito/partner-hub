package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing an external project.
 */
@Schema(description = "Response payload representing an external project.")
public class ExternalProjectResponseDTO {

    @Schema(description = "Unique identifier of the external project", example = "proj-001")
    private String id;

    @Schema(description = "Name of the external project", example = "Partner API Integration")
    private String name;

    @Schema(description = "Date and time when the project was created", example = "2024-01-15T10:00:00Z")
    private Instant createdAt;

    @Schema(description = "Date and time when the project was last updated", example = "2024-06-14T14:32:00Z")
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

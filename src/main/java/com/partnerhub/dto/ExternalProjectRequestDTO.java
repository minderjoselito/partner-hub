package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload to create a new external project for a user.
 */
@Schema(description = "Payload to create a new external project for a user.")
public class ExternalProjectRequestDTO {

    @NotBlank(message = "Project ID must not be blank")
    @Size(max = 200, message = "Project ID must be at most 200 characters")
    @Schema(
            description = "Unique identifier for the external project",
            example = "proj-001",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String id;

    @NotBlank(message = "Project name must not be blank")
    @Size(max = 120, message = "Project name must be at most 120 characters")
    @Schema(
            description = "Name of the external project",
            example = "Partner API Integration",
            maxLength = 120,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

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
}

package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload to create a new external project for a user.
 */
@Schema(description = "Payload to create a new external project for a user.")
public class ExternalProjectRequestDTO {

    @NotBlank
    @Size(max = 200)
    @Schema(description = "Unique identifier for the external project", example = "proj-001")
    private String id;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Name of the external project", example = "Partner API Integration")
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

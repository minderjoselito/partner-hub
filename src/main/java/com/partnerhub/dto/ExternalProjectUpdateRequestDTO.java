package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload to update the name of an existing external project.
 */
@Schema(description = "Payload to update the name of an existing external project.")
public class ExternalProjectUpdateRequestDTO {

    @NotBlank
    @Size(max = 120)
    @Schema(description = "The new name for the external project", example = "Partner API v2")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

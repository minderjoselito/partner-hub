package com.partnerhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload to update the name of an existing external project.
 */
public class ExternalProjectUpdateRequestDTO {

    @NotBlank(message = "Project name must not be blank")
    @Size(max = 120, message = "Project name must be at most 120 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

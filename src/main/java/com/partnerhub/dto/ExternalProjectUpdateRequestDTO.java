package com.partnerhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ExternalProjectUpdateRequestDTO {
    @NotBlank
    @Size(max = 120)
    private String name;
}

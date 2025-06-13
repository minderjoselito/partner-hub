package com.partnerhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExternalProjectRequestDTO {
    @NotBlank
    @Size(max = 200)
    private String id;

    @NotBlank
    @Size(max = 120)
    private String name;
}

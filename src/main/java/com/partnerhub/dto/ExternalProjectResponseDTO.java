package com.partnerhub.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ExternalProjectResponseDTO {
    private String id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}

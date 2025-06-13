package com.partnerhub.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}

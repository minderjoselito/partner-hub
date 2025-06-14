package com.partnerhub.mapper;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ExternalProjectMapperTest {

    private final ExternalProjectMapper mapper = Mappers.getMapper(ExternalProjectMapper.class);

    @Test
    void testToEntity() {
        ExternalProjectRequestDTO dto = new ExternalProjectRequestDTO();
        dto.setId("proj-123");
        dto.setName("My Project");

        ExternalProject entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("proj-123");
        assertThat(entity.getName()).isEqualTo("My Project");

        assertThat(entity.getUser()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void testToResponse() {
        ExternalProject entity = new ExternalProject();
        entity.setId("proj-123");
        entity.setName("My Project");
        entity.setCreatedAt(Instant.parse("2024-01-01T12:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2024-06-14T12:00:00Z"));

        ExternalProjectResponseDTO dto = mapper.toResponse(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("proj-123");
        assertThat(dto.getName()).isEqualTo("My Project");
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2024-01-01T12:00:00Z"));
        assertThat(dto.getUpdatedAt()).isEqualTo(Instant.parse("2024-06-14T12:00:00Z"));
    }
}

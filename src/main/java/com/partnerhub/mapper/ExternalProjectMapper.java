package com.partnerhub.mapper;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalProjectMapper {
    ExternalProjectResponseDTO toResponse(ExternalProject entity);
    ExternalProject toEntity(ExternalProjectRequestDTO dto);
}

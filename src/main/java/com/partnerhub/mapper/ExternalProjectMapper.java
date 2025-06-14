package com.partnerhub.mapper;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ExternalProjectMapper {
    @Mappings({
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    ExternalProject toEntity(ExternalProjectRequestDTO dto);

    ExternalProjectResponseDTO toResponse(ExternalProject entity);
}

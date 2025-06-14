package com.partnerhub.mapper;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "externalProjects", ignore = true)
    })
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponse(User user);
}

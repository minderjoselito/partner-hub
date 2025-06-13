package com.partnerhub.mapper;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.dto.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(User user);
    User toEntity(UserRequestDTO userRequestDTO);
}

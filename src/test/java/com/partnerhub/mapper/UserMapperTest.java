package com.partnerhub.mapper;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToEntity() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("josy@email.com");
        dto.setPassword("senha123");
        dto.setName("Josy");

        User user = mapper.toEntity(dto);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("josy@email.com");
        assertThat(user.getPassword()).isEqualTo("senha123");
        assertThat(user.getName()).isEqualTo("Josy");
    }

    @Test
    void testToResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ana@email.com");
        user.setName("Ana");
        user.setPassword("secreta");

        UserResponseDTO response = mapper.toResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("ana@email.com");
        assertThat(response.getName()).isEqualTo("Ana");
    }
}

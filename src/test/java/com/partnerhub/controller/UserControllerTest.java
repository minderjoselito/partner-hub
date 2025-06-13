package com.partnerhub.controller;

import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.dto.UserResponseDTO;
import com.partnerhub.mapper.UserMapper;
import com.partnerhub.service.UserService;
import com.partnerhub.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security for tests
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("newuser@mail.com");
        dto.setPassword("strongpassword");
        dto.setName("New User");

        User entity = new User();
        entity.setId(1L);
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setPassword(dto.getPassword());

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmail(dto.getEmail());
        responseDTO.setName(dto.getName());

        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(entity);
        when(userService.createUser(any(User.class))).thenReturn(entity);
        when(userMapper.toResponse(any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        long userId = 2L;
        User entity = new User();
        entity.setId(userId);
        entity.setEmail("user@mail.com");
        entity.setName("User");

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(userId);
        responseDTO.setEmail(entity.getEmail());
        responseDTO.setName(entity.getName());

        when(userService.findById(userId)).thenReturn(Optional.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(entity.getEmail()))
                .andExpect(jsonPath("$.name").value(entity.getName()));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        long userId = 999L;
        when(userService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        long userId = 10L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}

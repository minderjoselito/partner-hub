package com.partnerhub.controller;

import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.dto.UserResponseDTO;
import com.partnerhub.dto.UserUpdateRequestDTO;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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

    @Test
    void shouldReturnAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("a@b.com");
        user1.setName("User A");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("b@c.com");
        user2.setName("User B");

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        UserResponseDTO dto1 = new UserResponseDTO();
        dto1.setId(1L);
        dto1.setEmail("a@b.com");
        dto1.setName("User A");

        UserResponseDTO dto2 = new UserResponseDTO();
        dto2.setId(2L);
        dto2.setEmail("b@c.com");
        dto2.setName("User B");

        when(userMapper.toResponse(user1)).thenReturn(dto1);
        when(userMapper.toResponse(user2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("a@b.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("b@c.com"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("Updated Name");
        dto.setEmail("updated@email.com");

        User entity = new User();
        entity.setId(userId);
        entity.setName("Updated Name");
        entity.setEmail("updated@email.com");

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(userId);
        responseDTO.setName("Updated Name");
        responseDTO.setEmail("updated@email.com");

        when(userService.updateUser(eq(userId), any(UserUpdateRequestDTO.class))).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }
}

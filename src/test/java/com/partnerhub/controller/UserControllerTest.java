package com.partnerhub.controller;

import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.dto.UserResponseDTO;
import com.partnerhub.dto.UserUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
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

    // ===============================
    // CREATE USER TESTS
    // ===============================

    @Test
    void createUser_WhenValidData_ShouldReturnCreatedUser() throws Exception {
        // Given
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

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.id").value(1L));
    }

    // ===============================
    // GET USER TESTS
    // ===============================

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        long userId = 2L;
        User entity = new User();
        entity.setId(userId);
        entity.setEmail("user@mail.com");
        entity.setName("User");

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(userId);
        responseDTO.setEmail(entity.getEmail());
        responseDTO.setName(entity.getName());

        when(userService.findById(userId)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(entity.getEmail()))
                .andExpect(jsonPath("$.name").value(entity.getName()));
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        long userId = 999L;
        when(userService.findById(userId)).thenThrow(new NotFoundException("User with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"))
                .andExpect(jsonPath("$.path").value("/api/users/999"));
    }

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturnUserList() throws Exception {
        // Given
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

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("a@b.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("b@c.com"));
    }

    // ===============================
    // UPDATE USER TESTS
    // ===============================

    @Test
    void updateUser_WhenValidDataAndUserExists_ShouldReturnUpdatedUser() throws Exception {
        // Given
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

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    // ===============================
    // DELETE USER TESTS
    // ===============================

    @Test
    void deleteUser_WhenUserExists_ShouldReturn204() throws Exception {
        // Given
        Long userId = 10L;
        doNothing().when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        Long userId = 999L;
        doThrow(new NotFoundException("User with ID 999 not found"))
                .when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"))
                .andExpect(jsonPath("$.path").value("/api/users/999"));

        verify(userService, times(1)).deleteUser(userId);
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private UserRequestDTO createValidUserRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setName("Test User");
        return dto;
    }

    private User createTestUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    private UserResponseDTO createUserResponseDTO(Long id, String email, String name) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}
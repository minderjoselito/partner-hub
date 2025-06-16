package com.partnerhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.mapper.ExternalProjectMapper;
import com.partnerhub.service.ExternalProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExternalProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExternalProjectService externalProjectService;

    @MockitoBean
    private ExternalProjectMapper externalProjectMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // ADD PROJECT TESTS (POST)
    // ===============================

    @Test
    void addProject_WhenValidDataAndUserExists_ShouldReturnCreatedProject() throws Exception {
        // Given
        Long userId = 1L;
        ExternalProjectRequestDTO requestDTO = new ExternalProjectRequestDTO();
        requestDTO.setId("proj-001");
        requestDTO.setName("Partner API Integration");

        User user = createTestUser(userId, "user@test.com", "Test User");
        ExternalProject project = createTestProject("proj-001", "Partner API Integration", user);
        ExternalProjectResponseDTO responseDTO = createProjectResponseDTO("proj-001", "Partner API Integration");

        when(externalProjectMapper.toEntity(any(ExternalProjectRequestDTO.class))).thenReturn(project);
        when(externalProjectService.addProject(eq(userId), any(ExternalProject.class))).thenReturn(project);
        when(externalProjectMapper.toResponse(any(ExternalProject.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/projects", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("proj-001"))
                .andExpect(jsonPath("$.name").value("Partner API Integration"));

        verify(externalProjectMapper).toEntity(any(ExternalProjectRequestDTO.class));
        verify(externalProjectService).addProject(eq(userId), any(ExternalProject.class));
        verify(externalProjectMapper).toResponse(any(ExternalProject.class));
    }

    @Test
    void addProject_WhenProjectAlreadyExists_ShouldReturn409() throws Exception {
        // Given
        Long userId = 5L;
        ExternalProjectRequestDTO requestDTO = new ExternalProjectRequestDTO();
        requestDTO.setId("proj-409");
        requestDTO.setName("Duplicate Test");

        User user = createTestUser(userId, "user@test.com", "User");
        ExternalProject project = createTestProject("proj-409", "Duplicate Test", user);

        when(externalProjectMapper.toEntity(any(ExternalProjectRequestDTO.class))).thenReturn(project);
        when(externalProjectService.addProject(eq(userId), any(ExternalProject.class)))
                .thenThrow(new IllegalArgumentException("Project with ID proj-409 already exists for user with ID 5"));

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/projects", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Project with ID proj-409 already exists for user with ID 5"))
                .andExpect(jsonPath("$.path").value("/api/users/5/projects"));
    }

    @Test
    void addProject_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        Long userId = 999L;
        ExternalProjectRequestDTO requestDTO = new ExternalProjectRequestDTO();
        requestDTO.setId("proj-001");
        requestDTO.setName("Should Fail Project");

        when(externalProjectMapper.toEntity(any(ExternalProjectRequestDTO.class))).thenReturn(new ExternalProject());
        when(externalProjectService.addProject(eq(userId), any(ExternalProject.class)))
                .thenThrow(new NotFoundException("User with ID 999 not found"));

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/projects", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"))
                .andExpect(jsonPath("$.path").value("/api/users/999/projects"));
    }

    @Test
    void addProject_WhenInvalidData_ShouldReturn400() throws Exception {
        // Given
        Long userId = 1L;
        ExternalProjectRequestDTO requestDTO = new ExternalProjectRequestDTO();

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/projects", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    // ===============================
    // GET PROJECTS TESTS
    // ===============================

    @Test
    void getProjectsByUser_WhenUserHasProjects_ShouldReturnProjectList() throws Exception {
        // Given
        Long userId = 2L;
        User user = createTestUser(userId, "user@test.com", "Test User");

        ExternalProject project1 = createTestProject("proj-001", "Project Alpha", user);
        ExternalProject project2 = createTestProject("proj-002", "Project Beta", user);

        ExternalProjectResponseDTO dto1 = createProjectResponseDTO("proj-001", "Project Alpha");
        ExternalProjectResponseDTO dto2 = createProjectResponseDTO("proj-002", "Project Beta");

        when(externalProjectService.getProjectsByUserId(userId)).thenReturn(List.of(project1, project2));
        when(externalProjectMapper.toResponse(project1)).thenReturn(dto1);
        when(externalProjectMapper.toResponse(project2)).thenReturn(dto2);

        // When & Then
        mockMvc.perform(get("/api/users/{userId}/projects", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("proj-001"))
                .andExpect(jsonPath("$[0].name").value("Project Alpha"))
                .andExpect(jsonPath("$[1].id").value("proj-002"))
                .andExpect(jsonPath("$[1].name").value("Project Beta"));
    }

    @Test
    void getProjectsByUser_WhenUserHasNoProjects_ShouldReturnEmptyList() throws Exception {
        // Given
        Long userId = 3L;
        when(externalProjectService.getProjectsByUserId(userId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/users/{userId}/projects", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProjectsByUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        Long userId = 999L;
        when(externalProjectService.getProjectsByUserId(userId))
                .thenThrow(new NotFoundException("User with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/api/users/{userId}/projects", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"))
                .andExpect(jsonPath("$.path").value("/api/users/999/projects"));
    }

    // ===============================
    // UPDATE PROJECT TESTS (PUT)
    // ===============================

    @Test
    void updateProject_WhenValidDataAndProjectExists_ShouldReturnUpdatedProject() throws Exception {
        // Given
        Long userId = 1L;
        String projectId = "proj-001";
        ExternalProjectUpdateRequestDTO updateDTO = new ExternalProjectUpdateRequestDTO();
        updateDTO.setName("Updated Project Name");

        User user = createTestUser(userId, "user@test.com", "Test User");
        ExternalProject updatedProject = createTestProject(projectId, "Updated Project Name", user);
        ExternalProjectResponseDTO responseDTO = createProjectResponseDTO(projectId, "Updated Project Name");

        when(externalProjectService.updateProject(eq(userId), eq(projectId), any(ExternalProjectUpdateRequestDTO.class)))
                .thenReturn(updatedProject);
        when(externalProjectMapper.toResponse(updatedProject)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/users/{userId}/projects/{projectId}", userId, projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.name").value("Updated Project Name"));
    }

    @Test
    void updateProject_WhenProjectNotExists_ShouldReturn404() throws Exception {
        // Given
        Long userId = 1L;
        String projectId = "non-existent";
        ExternalProjectUpdateRequestDTO updateDTO = new ExternalProjectUpdateRequestDTO();
        updateDTO.setName("Some Name");

        when(externalProjectService.updateProject(eq(userId), eq(projectId), any(ExternalProjectUpdateRequestDTO.class)))
                .thenThrow(new NotFoundException("Project with ID non-existent or user with ID 1 not found"));

        // When & Then
        mockMvc.perform(put("/api/users/{userId}/projects/{projectId}", userId, projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Project with ID non-existent or user with ID 1 not found"))
                .andExpect(jsonPath("$.path").value("/api/users/1/projects/non-existent"));
    }

    @Test
    void updateProject_WhenInvalidData_ShouldReturn400() throws Exception {
        // Given
        Long userId = 1L;
        String projectId = "proj-001";
        ExternalProjectUpdateRequestDTO updateDTO = new ExternalProjectUpdateRequestDTO();

        // When & Then
        mockMvc.perform(put("/api/users/{userId}/projects/{projectId}", userId, projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private User createTestUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setEnabled(true);
        return user;
    }

    private ExternalProject createTestProject(String id, String name, User user) {
        ExternalProject project = new ExternalProject();
        project.setId(id);
        project.setName(name);
        project.setUser(user);
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        return project;
    }

    private ExternalProjectResponseDTO createProjectResponseDTO(String id, String name) {
        ExternalProjectResponseDTO dto = new ExternalProjectResponseDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());
        return dto;
    }
}
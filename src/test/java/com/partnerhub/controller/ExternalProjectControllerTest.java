package com.partnerhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.mapper.ExternalProjectMapper;
import com.partnerhub.service.ExternalProjectService;
import com.partnerhub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalProjectController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security for tests
class ExternalProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExternalProjectService externalProjectService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ExternalProjectMapper externalProjectMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAddExternalProjectToUser() throws Exception {
        Long userId = 1L;

        ExternalProjectRequestDTO requestDTO = new ExternalProjectRequestDTO();
        requestDTO.setId("p1");
        requestDTO.setName("Project 1");

        User user = new User();
        user.setId(userId);

        ExternalProject project = new ExternalProject();
        project.setId("p1");
        project.setName("Project 1");
        project.setUser(user);

        ExternalProjectResponseDTO responseDTO = new ExternalProjectResponseDTO();
        responseDTO.setId("p1");
        responseDTO.setName("Project 1");

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(externalProjectMapper.toEntity(any(ExternalProjectRequestDTO.class))).thenReturn(project);
        when(externalProjectService.addProject(any(ExternalProject.class))).thenReturn(project);
        when(externalProjectMapper.toResponse(any(ExternalProject.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users/{userId}/projects", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Project 1"));
    }

    @Test
    void shouldListExternalProjectsByUser() throws Exception {
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        ExternalProject project1 = new ExternalProject();
        project1.setId("p1");
        project1.setName("Project 1");
        project1.setUser(user);

        ExternalProject project2 = new ExternalProject();
        project2.setId("p2");
        project2.setName("Project 2");
        project2.setUser(user);

        ExternalProjectResponseDTO dto1 = new ExternalProjectResponseDTO();
        dto1.setId("p1");
        dto1.setName("Project 1");

        ExternalProjectResponseDTO dto2 = new ExternalProjectResponseDTO();
        dto2.setId("p2");
        dto2.setName("Project 2");

        when(externalProjectService.getProjectsByUserId(userId)).thenReturn(List.of(project1, project2));
        when(externalProjectMapper.toResponse(project1)).thenReturn(dto1);
        when(externalProjectMapper.toResponse(project2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users/{userId}/projects", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[0].name").value("Project 1"))
                .andExpect(jsonPath("$[1].id").value("p2"))
                .andExpect(jsonPath("$[1].name").value("Project 2"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Long userId = 3L;
        ExternalProjectRequestDTO requestDTO = new ExternalProjectRequestDTO();
        requestDTO.setId("notfound");
        requestDTO.setName("Should Fail");

        when(userService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/{userId}/projects", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
    }
}

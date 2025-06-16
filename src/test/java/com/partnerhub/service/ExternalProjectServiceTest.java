package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.ExternalProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalProjectServiceTest {

    private ExternalProjectRepository externalProjectRepository;
    private UserService userService;
    private ExternalProjectService externalProjectService;

    @BeforeEach
    void setUp() {
        externalProjectRepository = mock(ExternalProjectRepository.class);
        userService = mock(UserService.class);
        externalProjectService = new ExternalProjectService(externalProjectRepository, userService);
    }

    // ===============================
    // ADD PROJECT TESTS
    // ===============================

    @Test
    void addProject_WhenUserExistsAndProjectIsUnique_ShouldReturnSavedProject() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("a@b.com");

        ExternalProject project = new ExternalProject();
        project.setId("p1");
        project.setName("Project 1");

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(externalProjectRepository.findById(project.getId())).thenReturn(Optional.empty());
        when(externalProjectRepository.save(any(ExternalProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExternalProject saved = externalProjectService.addProject(userId, project);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Project 1");
        assertThat(saved.getUser()).isEqualTo(user);

        verify(userService).findById(userId);
        verify(externalProjectRepository).findById(project.getId());
        verify(externalProjectRepository).save(project);
    }

    @Test
    void addProject_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        Long userId = 777L;
        ExternalProject project = new ExternalProject();
        project.setId("pX");
        project.setName("Project X");

        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> externalProjectService.addProject(userId, project))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with ID 777 not found");

        verify(userService).findById(userId);
        verify(externalProjectRepository, never()).save(any());
    }

    @Test
    void addProject_WhenProjectAlreadyExistsForUser_ShouldThrowIllegalArgumentException() {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);

        ExternalProject existingProject = new ExternalProject();
        existingProject.setId("p1");
        existingProject.setUser(user);

        ExternalProject newProject = new ExternalProject();
        newProject.setId("p1");
        newProject.setName("New Project");
        // Will set user in the service

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(externalProjectRepository.findById("p1")).thenReturn(Optional.of(existingProject));

        assertThatThrownBy(() -> externalProjectService.addProject(userId, newProject))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists for user with ID 2");

        verify(userService).findById(userId);
        verify(externalProjectRepository).findById("p1");
        verify(externalProjectRepository, never()).save(any());
    }

    // ===============================
    // GET PROJECTS TESTS
    // ===============================

    @Test
    void getProjectsByUserId_WhenProjectsExist_ShouldReturnProjectsList() {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        user.setEmail("b@c.com");

        ExternalProject p1 = new ExternalProject();
        p1.setId("p1");
        p1.setName("X");
        p1.setUser(user);

        ExternalProject p2 = new ExternalProject();
        p2.setId("p2");
        p2.setName("Y");
        p2.setUser(user);

        when(externalProjectRepository.findByUserId(userId)).thenReturn(List.of(p1, p2));

        List<ExternalProject> projects = externalProjectService.getProjectsByUserId(userId);

        assertThat(projects).hasSize(2);
        assertThat(projects).containsExactly(p1, p2);
        verify(externalProjectRepository).findByUserId(userId);
    }

    @Test
    void getProjectsByUserId_WhenNoProjects_ShouldReturnEmptyList() {
        when(externalProjectRepository.findByUserId(333L)).thenReturn(List.of());

        List<ExternalProject> projects = externalProjectService.getProjectsByUserId(333L);

        assertThat(projects).isEmpty();
        verify(externalProjectRepository).findByUserId(333L);
    }

    // ===============================
    // UPDATE PROJECT TESTS
    // ===============================

    @Test
    void updateProject_WhenValidDataAndProjectBelongsToUser_ShouldReturnUpdatedProject() {
        Long userId = 1L;
        String projectId = "p-1";

        User user = new User();
        user.setId(userId);

        ExternalProject project = new ExternalProject();
        project.setId(projectId);
        project.setName("Old Name");
        project.setUser(user);

        ExternalProjectUpdateRequestDTO dto = new ExternalProjectUpdateRequestDTO();
        dto.setName("New Project Name");

        when(externalProjectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExternalProject result = externalProjectService.updateProject(userId, projectId, dto);

        assertThat(result.getName()).isEqualTo("New Project Name");
        assertThat(result.getUser()).isEqualTo(user);
        verify(externalProjectRepository).findById(projectId);
        verify(externalProjectRepository).save(project);
    }

    @Test
    void updateProject_WhenProjectNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        String projectId = "not-exist";
        ExternalProjectUpdateRequestDTO dto = new ExternalProjectUpdateRequestDTO();
        dto.setName("New Name");

        when(externalProjectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> externalProjectService.updateProject(userId, projectId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project with ID not-exist not found");

        verify(externalProjectRepository).findById(projectId);
        verify(externalProjectRepository, never()).save(any());
    }

    @Test
    void updateProject_WhenProjectDoesNotBelongToUser_ShouldThrowNotFoundException() {
        Long userId = 123L;
        String projectId = "p-1";
        ExternalProject existingProject = new ExternalProject();
        existingProject.setId(projectId);
        existingProject.setName("Project");
        User otherUser = new User();
        otherUser.setId(999L);
        existingProject.setUser(otherUser);

        ExternalProjectUpdateRequestDTO dto = new ExternalProjectUpdateRequestDTO();
        dto.setName("Updated");

        when(externalProjectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        assertThatThrownBy(() -> externalProjectService.updateProject(userId, projectId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("does not belong to user with ID 123");

        verify(externalProjectRepository).findById(projectId);
        verify(externalProjectRepository, never()).save(any());
    }
}
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ExternalProjectServiceTest {

    private ExternalProjectRepository externalProjectRepository;
    private ExternalProjectService externalProjectService;

    @BeforeEach
    void setUp() {
        externalProjectRepository = mock(ExternalProjectRepository.class);
        externalProjectService = new ExternalProjectService(externalProjectRepository);
    }

    @Test
    void shouldAddProject() {
        User user = User.builder().id(1L).email("a@b.com").build();
        ExternalProject project = ExternalProject.builder()
                .id("p1").name("Project 1").user(user).build();

        when(externalProjectRepository.save(project)).thenReturn(project);

        ExternalProject saved = externalProjectService.addProject(project);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Project 1");
        verify(externalProjectRepository).save(project);
    }

    @Test
    void shouldGetProjectsByUserId() {
        User user = User.builder().id(2L).email("b@c.com").build();
        ExternalProject p1 = ExternalProject.builder().id("p1").name("X").user(user).build();
        ExternalProject p2 = ExternalProject.builder().id("p2").name("Y").user(user).build();

        when(externalProjectRepository.findByUserId(2L)).thenReturn(List.of(p1, p2));

        List<ExternalProject> projects = externalProjectService.getProjectsByUserId(2L);

        assertThat(projects).hasSize(2);
        verify(externalProjectRepository).findByUserId(2L);
    }

    @Test
    void shouldUpdateProjectSuccessfully() {
        String projectId = "p-1";
        ExternalProject existingProject = new ExternalProject();
        existingProject.setId(projectId);
        existingProject.setName("Old Project");

        ExternalProjectUpdateRequestDTO dto = new ExternalProjectUpdateRequestDTO();
        dto.setName("Updated Project");

        when(externalProjectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExternalProject result = externalProjectService.updateProject(projectId, dto);

        assertThat(result.getName()).isEqualTo("Updated Project");
        verify(externalProjectRepository).save(existingProject);
    }

    @Test
    void shouldThrowWhenProjectNotFoundOnUpdate() {
        String projectId = "not-exist";
        ExternalProjectUpdateRequestDTO dto = new ExternalProjectUpdateRequestDTO();
        dto.setName("New Name");

        when(externalProjectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> externalProjectService.updateProject(projectId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
    }
}

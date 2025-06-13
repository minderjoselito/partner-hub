package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.repository.ExternalProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExternalProjectServiceTest {

    private ExternalProjectRepository repository;
    private ExternalProjectService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExternalProjectRepository.class);
        service = new ExternalProjectService(repository);
    }

    @Test
    void shouldAddProject() {
        User user = User.builder().id(1L).email("a@b.com").build();
        ExternalProject project = ExternalProject.builder()
                .id("p1").name("Project 1").user(user).build();

        when(repository.save(project)).thenReturn(project);

        ExternalProject saved = service.addProject(project);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Project 1");
        verify(repository).save(project);
    }

    @Test
    void shouldGetProjectsByUserId() {
        User user = User.builder().id(2L).email("b@c.com").build();
        ExternalProject p1 = ExternalProject.builder().id("p1").name("X").user(user).build();
        ExternalProject p2 = ExternalProject.builder().id("p2").name("Y").user(user).build();

        when(repository.findByUserId(2L)).thenReturn(List.of(p1, p2));

        List<ExternalProject> projects = service.getProjectsByUserId(2L);

        assertThat(projects).hasSize(2);
        verify(repository).findByUserId(2L);
    }
}

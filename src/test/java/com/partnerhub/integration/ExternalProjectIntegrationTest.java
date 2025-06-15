package com.partnerhub.integration;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.repository.ExternalProjectRepository;
import com.partnerhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class ExternalProjectIntegrationTest extends PostgresTestContainer {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExternalProjectRepository projectRepository;

    private User user;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setEmail("a@b.com");
        u.setPassword("password");
        u.setName("TestContainerUser");
        user = userRepository.save(u);
    }

    @Test
    void shouldSaveAndFindProjectByUser() {
        ExternalProject project = new ExternalProject();
        project.setId("p1");
        project.setName("Project 1");
        project.setUser(user);

        projectRepository.save(project);

        List<ExternalProject> projects = projectRepository.findByUserId(user.getId());
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Project 1");
    }
}

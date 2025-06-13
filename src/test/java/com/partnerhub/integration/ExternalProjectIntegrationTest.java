package com.partnerhub.integration;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.repository.ExternalProjectRepository;
import com.partnerhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ExternalProjectIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExternalProjectRepository projectRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(
                User.builder().email("a@b.com").password("password").name("TestContainerUser").build()
        );
    }

    @Test
    void shouldSaveAndFindProjectByUser() {
        ExternalProject project = ExternalProject.builder()
                .id("p1")
                .name("Project 1")
                .user(user)
                .build();

        projectRepository.save(project);

        List<ExternalProject> projects = projectRepository.findByUserId(user.getId());
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Project 1");
    }
}

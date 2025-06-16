package com.partnerhub.integration;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.repository.ExternalProjectRepository;
import com.partnerhub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AllIntegrationTest extends PostgresTestContainer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExternalProjectRepository projectRepository;

    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User();
        user.setEmail("a@b.com");
        user.setPassword("password");
        user.setName("TestContainerUser");

        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void shouldSaveAndFindProjectByUser() {
        User user = new User();
        user.setEmail("b@c.com");
        user.setPassword("password");
        user.setName("TestContainerUser2");

        user = userRepository.save(user);

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


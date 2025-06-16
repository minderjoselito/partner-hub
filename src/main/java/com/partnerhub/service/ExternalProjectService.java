package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.ExternalProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalProjectService {

    private static final Logger log = LoggerFactory.getLogger(ExternalProjectService.class);

    private final ExternalProjectRepository externalProjectRepository;
    private final UserService userService;

    public ExternalProjectService(ExternalProjectRepository externalProjectRepository, UserService userService) {
        this.externalProjectRepository = externalProjectRepository;
        this.userService = userService;
    }

    @Transactional
    public ExternalProject addProject(Long userId, ExternalProject project) {
        log.info("Attempting to add external project with ID {} for user ID {}", project.getId(), userId);

        User user = userService.findById(userId);
        project.setUser(user);

        Optional<ExternalProject> existing = externalProjectRepository.findById(project.getId());
        if (existing.isPresent() &&
                existing.get().getUser() != null &&
                existing.get().getUser().getId().equals(userId)) {
            log.warn("External project with ID {} already exists for user ID {}", project.getId(), userId);
            throw new IllegalArgumentException(
                    String.format("Project with ID %s already exists for user with ID %d", project.getId(), userId)
            );
        }

        ExternalProject saved = externalProjectRepository.save(project);
        log.info("External project saved with ID {} for user ID {}", saved.getId(), userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ExternalProject> getProjectsByUserId(Long userId) {
        log.info("Fetching external projects for user ID {}", userId);
        List<ExternalProject> projects = externalProjectRepository.findByUserId(userId);
        log.info("Found {} projects for user ID {}", projects.size(), userId);
        return projects;
    }

    @Transactional
    public ExternalProject updateProject(Long userId, String projectId, ExternalProjectUpdateRequestDTO dto) {
        log.info("Attempting to update project ID {} for user ID {}", projectId, userId);

        User user = userService.findById(userId);

        ExternalProject project = externalProjectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found with ID {}", projectId);
                    return new NotFoundException(String.format("Project with ID %s not found", projectId));
                });

        if (project.getUser() == null || !project.getUser().getId().equals(userId)) {
            log.warn("Project {} does not belong to user ID {}", projectId, userId);
            throw new NotFoundException(String.format("Project with ID %s does not belong to user with ID %d", projectId, userId));
        }

        project.setName(dto.getName());
        ExternalProject updated = externalProjectRepository.save(project);

        log.info("Project {} updated successfully for user ID {}", projectId, userId);
        return updated;
    }
}
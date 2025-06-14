package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.ExternalProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalProjectService {

    private static final Logger log = LoggerFactory.getLogger(ExternalProjectService.class);

    private final ExternalProjectRepository externalProjectRepository;

    public ExternalProjectService(ExternalProjectRepository externalProjectRepository) {
        this.externalProjectRepository = externalProjectRepository;
    }

    @Transactional
    public ExternalProject addProject(ExternalProject project) {
        log.info("Saving new external project with ID {} for user ID {}",
                project.getId(), project.getUser() != null ? project.getUser().getId() : null);
        ExternalProject saved = externalProjectRepository.save(project);
        log.info("External project saved with ID {}", saved.getId());
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

        ExternalProject project = externalProjectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found with ID {}", projectId);
                    return new NotFoundException("Project not found");
                });

        if (project.getUser() == null || !project.getUser().getId().equals(userId)) {
            log.warn("Project {} does not belong to user ID {}", projectId, userId);
            throw new NotFoundException("Project does not belong to this user");
        }

        project.setName(dto.getName());
        ExternalProject updated = externalProjectRepository.save(project);

        log.info("Project {} updated successfully for user ID {}", projectId, userId);
        return updated;
    }
}

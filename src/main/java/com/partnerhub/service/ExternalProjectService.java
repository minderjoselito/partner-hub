package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.ExternalProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalProjectService {

    private final ExternalProjectRepository externalProjectRepository;

    public ExternalProjectService(ExternalProjectRepository externalProjectRepository) {
        this.externalProjectRepository = externalProjectRepository;
    }

    @Transactional
    public ExternalProject addProject(ExternalProject project) {
        return externalProjectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<ExternalProject> getProjectsByUserId(Long userId) {
        return externalProjectRepository.findByUserId(userId);
    }

    @Transactional
    public ExternalProject updateProject(Long userId, String projectId, ExternalProjectUpdateRequestDTO dto) {
        ExternalProject project = externalProjectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        if (project.getUser() == null || !project.getUser().getId().equals(userId)) {
            throw new NotFoundException("Project does not belong to this user");
        }

        project.setName(dto.getName());
        return externalProjectRepository.save(project);
    }
}

package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
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
}

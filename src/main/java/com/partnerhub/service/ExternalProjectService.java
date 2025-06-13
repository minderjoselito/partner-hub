package com.partnerhub.service;

import com.partnerhub.domain.ExternalProject;
import com.partnerhub.repository.ExternalProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalProjectService {

    private final ExternalProjectRepository repository;

    public ExternalProjectService(ExternalProjectRepository externalProjectRepository) {
        this.repository = externalProjectRepository;
    }

    @Transactional
    public ExternalProject addProject(ExternalProject project) {
        return repository.save(project);
    }

    @Transactional(readOnly = true)
    public List<ExternalProject> getProjectsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}

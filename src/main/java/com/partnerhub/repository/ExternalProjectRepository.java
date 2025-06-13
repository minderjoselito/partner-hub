package com.partnerhub.repository;

import com.partnerhub.domain.ExternalProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalProjectRepository extends JpaRepository<ExternalProject, String> {
    List<ExternalProject> findByUserId(Long userId);
}

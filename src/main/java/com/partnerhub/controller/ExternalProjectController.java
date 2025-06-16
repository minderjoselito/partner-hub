package com.partnerhub.controller;

import com.partnerhub.dto.*;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.mapper.ExternalProjectMapper;
import com.partnerhub.service.ExternalProjectService;
import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints for managing user's external projects.
 */
@RestController
@RequestMapping("/api/users/{userId}/projects")
public class ExternalProjectController {

    private static final Logger log = LoggerFactory.getLogger(ExternalProjectController.class);

    private final ExternalProjectService externalProjectService;
    private final UserService userService;
    private final ExternalProjectMapper externalProjectMapper;

    public ExternalProjectController(
            ExternalProjectService externalProjectService,
            UserService userService,
            ExternalProjectMapper externalProjectMapper
    ) {
        this.externalProjectService = externalProjectService;
        this.userService = userService;
        this.externalProjectMapper = externalProjectMapper;
    }

    @PostMapping
    public ResponseEntity<ExternalProjectResponseDTO> addProject(
            @PathVariable Long userId,
            @Valid @RequestBody ExternalProjectRequestDTO requestDTO
    ) {
        log.info("Creating external project {} for user ID {}", requestDTO.getId(), userId);

        User user = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new NotFoundException("User not found");
                });

        ExternalProject project = externalProjectMapper.toEntity(requestDTO);
        project.setUser(user);
        ExternalProject saved = externalProjectService.addProject(project);

        log.info("External project {} created for user ID {}", saved.getId(), userId);

        ExternalProjectResponseDTO responseDTO = externalProjectMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ExternalProjectResponseDTO>> getProjectsByUser(
            @PathVariable Long userId
    ) {
        log.info("Fetching all external projects for user ID: {}", userId);
        List<ExternalProject> projects = externalProjectService.getProjectsByUserId(userId);
        log.info("Found {} external projects for user ID {}", projects.size(), userId);

        List<ExternalProjectResponseDTO> result = projects.stream()
                .map(externalProjectMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ExternalProjectResponseDTO> updateProject(
            @PathVariable Long userId,
            @PathVariable String projectId,
            @Valid @RequestBody ExternalProjectUpdateRequestDTO dto
    ) {
        log.info("Updating project {} for user ID {}", projectId, userId);

        ExternalProject updated = externalProjectService.updateProject(userId, projectId, dto);

        log.info("Project {} updated for user ID {}", projectId, userId);

        ExternalProjectResponseDTO response = externalProjectMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
}

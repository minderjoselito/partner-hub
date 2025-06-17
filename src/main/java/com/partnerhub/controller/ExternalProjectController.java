package com.partnerhub.controller;

import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.mapper.ExternalProjectMapper;
import com.partnerhub.service.ExternalProjectService;
import com.partnerhub.domain.ExternalProject;

import jakarta.validation.Valid;
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

    private final ExternalProjectService externalProjectService;
    private final ExternalProjectMapper externalProjectMapper;

    public ExternalProjectController(
            ExternalProjectService externalProjectService,
            ExternalProjectMapper externalProjectMapper
    ) {
        this.externalProjectService = externalProjectService;
        this.externalProjectMapper = externalProjectMapper;
    }

    @PostMapping
    public ResponseEntity<ExternalProjectResponseDTO> addProject(
            @PathVariable Long userId,
            @Valid @RequestBody ExternalProjectRequestDTO requestDTO
    ) {
        ExternalProject project = externalProjectMapper.toEntity(requestDTO);
        ExternalProject saved = externalProjectService.addProject(userId, project);
        ExternalProjectResponseDTO responseDTO = externalProjectMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ExternalProjectResponseDTO>> getProjectsByUser(
            @PathVariable Long userId
    ) {
        List<ExternalProject> projects = externalProjectService.getProjectsByUserId(userId);
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
        ExternalProject updated = externalProjectService.updateProject(userId, projectId, dto);
        ExternalProjectResponseDTO response = externalProjectMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
}
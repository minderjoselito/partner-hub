package com.partnerhub.controller;

import com.partnerhub.dto.ExternalProjectRequestDTO;
import com.partnerhub.dto.ExternalProjectResponseDTO;
import com.partnerhub.dto.ExternalProjectUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.mapper.ExternalProjectMapper;
import com.partnerhub.service.ExternalProjectService;
import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing external projects linked to users.
 */
@RestController
@RequestMapping("/api/users/{userId}/projects")
public class ExternalProjectController {

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

    /**
     * Adds a new external project to a user.
     *
     * @param userId The user ID.
     * @param requestDTO The project data.
     * @return The created project.
     */
    @PostMapping
    public ResponseEntity<ExternalProjectResponseDTO> addProject(
            @PathVariable Long userId,
            @Valid @RequestBody ExternalProjectRequestDTO requestDTO
    ) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ExternalProject project = externalProjectMapper.toEntity(requestDTO);
        project.setUser(user);
        ExternalProject saved = externalProjectService.addProject(project);
        ExternalProjectResponseDTO responseDTO = externalProjectMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Lists all external projects of a user.
     *
     * @param userId The user ID.
     * @return List of projects.
     */
    @GetMapping
    public ResponseEntity<List<ExternalProjectResponseDTO>> getProjectsByUser(@PathVariable Long userId) {
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

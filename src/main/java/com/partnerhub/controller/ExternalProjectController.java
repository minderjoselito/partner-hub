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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/projects")
@Tag(name = "External Projects", description = "Endpoints for managing user's external projects")
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

    @PostMapping
    @Operation(
            summary = "Add a new external project to a user",
            description = "Creates a new project and links it to the specified user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Project successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<ExternalProjectResponseDTO> addProject(
            @Parameter(description = "ID of the user to associate the project with", example = "1")
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

    @GetMapping
    @Operation(
            summary = "Get all external projects of a user",
            description = "Returns a list of projects associated with the specified user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of projects returned successfully")
            }
    )
    public ResponseEntity<List<ExternalProjectResponseDTO>> getProjectsByUser(
            @Parameter(description = "ID of the user", example = "1")
            @PathVariable Long userId
    ) {
        List<ExternalProject> projects = externalProjectService.getProjectsByUserId(userId);
        List<ExternalProjectResponseDTO> result = projects.stream()
                .map(externalProjectMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{projectId}")
    @Operation(
            summary = "Update a user's external project",
            description = "Updates an existing project by user ID and project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Project or user not found")
            }
    )
    public ResponseEntity<ExternalProjectResponseDTO> updateProject(
            @Parameter(description = "ID of the user", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "ID of the project to update", example = "proj-001")
            @PathVariable String projectId,

            @Valid @RequestBody ExternalProjectUpdateRequestDTO dto
    ) {
        ExternalProject updated = externalProjectService.updateProject(userId, projectId, dto);
        ExternalProjectResponseDTO response = externalProjectMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
}

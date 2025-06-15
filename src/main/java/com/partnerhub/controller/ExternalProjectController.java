package com.partnerhub.controller;

import com.partnerhub.dto.*;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.mapper.ExternalProjectMapper;
import com.partnerhub.service.ExternalProjectService;
import com.partnerhub.domain.ExternalProject;
import com.partnerhub.domain.User;
import com.partnerhub.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/projects")
@Tag(name = "External Projects", description = "Endpoints for managing user's external projects")
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
    @Operation(
            summary = "Add a new external project to a user",
            description = "Creates a new project and links it to the specified user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Project successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExternalProjectResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"id\": \"proj-001\", \"name\": \"Partner API Integration\", \"createdAt\": \"2024-01-15T10:00:00Z\", \"updatedAt\": \"2024-06-14T14:32:00Z\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 400, \"errors\": [ { \"field\": \"id\", \"message\": \"Project ID must not be blank\", \"rejectedValue\": \"\" } ], \"path\": \"/api/users/1/projects\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users/1/projects\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User with ID 1 not found\", \"path\": \"/api/users/1/projects\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ExternalProjectResponseDTO> addProject(
            @Parameter(description = "ID of the user to associate the project with", example = "1")
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
    @Operation(
            summary = "Get all external projects of a user",
            description = "Returns a list of projects associated with the specified user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of projects returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExternalProjectResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users/1/projects\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User with ID 1 not found\", \"path\": \"/api/users/1/projects\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<ExternalProjectResponseDTO>> getProjectsByUser(
            @Parameter(description = "ID of the user", example = "1")
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
    @Operation(
            summary = "Update a user's external project",
            description = "Updates an existing project by user ID and project ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExternalProjectResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"id\": \"proj-001\", \"name\": \"Partner API v2\", \"createdAt\": \"2024-01-15T10:00:00Z\", \"updatedAt\": \"2024-06-15T10:10:00Z\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 400, \"errors\": [ { \"field\": \"name\", \"message\": \"Project name must not be blank\", \"rejectedValue\": \"\" } ], \"path\": \"/api/users/1/projects/proj-001\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users/1/projects/proj-001\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project or user not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Project with ID proj-001 or user with ID 1 not found\", \"path\": \"/api/users/1/projects/proj-001\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ExternalProjectResponseDTO> updateProject(
            @Parameter(description = "ID of the user", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "ID of the project to update", example = "proj-001")
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

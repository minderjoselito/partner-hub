package com.partnerhub.controller;

import com.partnerhub.dto.*;
import com.partnerhub.mapper.UserMapper;
import com.partnerhub.service.UserService;
import com.partnerhub.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a user using the provided email, password, and name",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"id\": 42, \"email\": \"john.doe@example.com\", \"name\": \"John Doe\", \"createdAt\": \"2024-01-15T10:00:00Z\", \"updatedAt\": \"2024-06-14T14:32:00Z\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 400, \"errors\": [ { \"field\": \"email\", \"message\": \"Email must be valid\", \"rejectedValue\": \"invalid\" } ], \"path\": \"/api/users\" }"
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
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO requestDTO
    ) {
        log.info("Creating user with email: {}", requestDTO.getEmail());
        User user = userMapper.toEntity(requestDTO);
        User createdUser = userService.createUser(user);
        UserResponseDTO responseDTO = userMapper.toResponse(createdUser);
        log.info("User created with ID: {}", responseDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Returns the user that matches the given ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"id\": 42, \"email\": \"john.doe@example.com\", \"name\": \"John Doe\", \"createdAt\": \"2024-01-15T10:00:00Z\", \"updatedAt\": \"2024-06-14T14:32:00Z\" }"
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
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User with ID 42 not found\", \"path\": \"/api/users/42\" }"
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
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users/42\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "1")
            @PathVariable Long id
    ) {
        log.info("Fetching user with ID: {}", id);
        return userService.findById(id)
                .map(user -> {
                    log.info("User found: {}", user.getEmail());
                    return ResponseEntity.ok(userMapper.toResponse(user));
                })
                .orElseGet(() -> {
                    log.warn("User not found with ID: {}", id);
                    // Aqui você pode retornar um erro padronizado, se preferir
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Deletes the user associated with the given ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User with ID 42 not found\", \"path\": \"/api/users/42\" }"
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
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users/42\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "1")
            @PathVariable Long id
    ) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        log.info("User with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Users retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponseDTO> users = userService.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        log.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user by ID",
            description = "Updates a user's name or email using the provided data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"id\": 42, \"email\": \"john.doe@example.com\", \"name\": \"John Doe\", \"createdAt\": \"2024-01-15T10:00:00Z\", \"updatedAt\": \"2024-06-14T14:32:00Z\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 400, \"errors\": [ { \"field\": \"name\", \"message\": \"Name must not be blank\", \"rejectedValue\": \"\" } ], \"path\": \"/api/users/42\" }"
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
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User with ID 42 not found\", \"path\": \"/api/users/42\" }"
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
                                            value = "{ \"timestamp\": \"2025-06-15T17:02:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource\", \"path\": \"/api/users/42\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO dto
    ) {
        log.info("Updating user ID: {} with new email: {}", id, dto.getEmail());
        User updated = userService.updateUser(id, dto);
        UserResponseDTO response = userMapper.toResponse(updated);
        log.info("User with ID {} updated", id);
        return ResponseEntity.ok(response);
    }
}

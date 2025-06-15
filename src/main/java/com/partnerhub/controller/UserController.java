package com.partnerhub.controller;

import com.partnerhub.dto.*;
import com.partnerhub.mapper.UserMapper;
import com.partnerhub.service.UserService;
import com.partnerhub.domain.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
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
    public ResponseEntity<UserResponseDTO> getUserById(
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
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        log.info("User with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
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
    public ResponseEntity<UserResponseDTO> updateUser(
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

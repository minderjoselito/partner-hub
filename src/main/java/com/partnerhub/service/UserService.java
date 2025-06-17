package com.partnerhub.service;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.UserRepository;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(User user) {
        log.info("Attempting to create user with email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email {} is already registered", user.getEmail());
            throw new IllegalArgumentException("Email has already been registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        log.info("Looking for user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new NotFoundException(String.format("User with ID %d not found", id));
                });
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("Retrieving all users");
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("User with ID {} not found", id);
            throw new NotFoundException(String.format("User with ID %d not found", id));
        }

        userRepository.deleteById(id);
        log.info("User with ID {} successfully deleted", id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.info("Searching for user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequestDTO dto) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new NotFoundException(String.format("User with ID %d not found", id));
                });

        if (!user.getEmail().equals(dto.getEmail())) {
            userRepository.findByEmail(dto.getEmail())
                    .ifPresent(u -> {
                        log.warn("Email {} is already registered to another user", dto.getEmail());
                        throw new IllegalArgumentException("Email has already been registered");
                    });
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User updatedUser = userRepository.save(user);
        log.info("User with ID: {} updated successfully", updatedUser.getId());
        return updatedUser;
    }
}

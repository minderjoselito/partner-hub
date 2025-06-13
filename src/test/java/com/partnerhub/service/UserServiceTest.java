package com.partnerhub.service;

import com.partnerhub.domain.User;
import com.partnerhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldCreateUser_WhenEmailNotExists() {
        User user = User.builder()
                .email("a@b.com")
                .password("plainpass")
                .name("Test")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpass");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        User saved = userService.createUser(user);

        assertThat(saved).isNotNull();
        verify(passwordEncoder).encode("plainpass");
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowException_WhenEmailExists() {
        User user = User.builder()
                .email("a@b.com")
                .password("plainpass")
                .name("Test")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email has already been registered");
    }
}

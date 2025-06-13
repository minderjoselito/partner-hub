package com.partnerhub.service;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
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

    @Test
    void shouldFindUserById() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldFindAllUsers() {
        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    void shouldDeleteUserById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("a@b.com");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("a@b.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("a@b.com");
        verify(userRepository).findByEmail("a@b.com");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");
        existingUser.setPassword("hashedpassword");

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("New Name");
        dto.setEmail("new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(userId, dto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@email.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldThrowException_WhenUserNotFoundOnUpdate() {
        Long userId = 999L;
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("Any");
        dto.setEmail("any@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyExists() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("New Name");
        dto.setEmail("duplicate@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email has already been registered");
    }
}

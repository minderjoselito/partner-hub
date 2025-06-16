package com.partnerhub.service;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserUpdateRequestDTO;
import com.partnerhub.exception.NotFoundException;
import com.partnerhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    // ===============================
    // CREATE USER TESTS
    // ===============================

    @Test
    void createUser_WhenEmailIsUnique_ShouldReturnCreatedUser() {
        // Given
        User user = new User();
        user.setEmail("a@b.com");
        user.setPassword("plainpass");
        user.setName("Test");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        // When
        User saved = userService.createUser(user);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("a@b.com");
        assertThat(saved.getName()).isEqualTo("Test");
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowIllegalArgumentException() {
        // Given
        User user = new User();
        user.setEmail("a@b.com");
        user.setPassword("plainpass");
        user.setName("Test");

        User existingUser = new User();
        existingUser.setEmail("a@b.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email has already been registered");

        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any());
    }

    // ===============================
    // FIND USER TESTS
    // ===============================

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(userId);
    }

    @Test
    void findAll_WhenUsersExist_ShouldReturnUserList() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@test.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // When
        List<User> result = userService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(user1, user2);
        verify(userRepository).findAll();
    }

    @Test
    void findByEmail_WhenEmailExists_ShouldReturnUser() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getName()).isEqualTo("Test User");
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WhenEmailNotExists_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(email);
    }

    // ===============================
    // UPDATE USER TESTS
    // ===============================

    @Test
    void updateUser_WhenValidDataAndUserExists_ShouldReturnUpdatedUser() {
        // Given
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

        // When
        User result = userService.updateUser(userId, dto);

        // Then
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@email.com");
        assertThat(result.getId()).isEqualTo(userId);
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Given
        Long userId = 999L;
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("Any Name");
        dto.setEmail("any@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WhenEmailBelongsToAnotherUser_ShouldThrowIllegalArgumentException() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("duplicate@email.com");

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("New Name");
        dto.setEmail("duplicate@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(anotherUser));

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email has already been registered");

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WhenEmailIsSameAsCurrentUser_ShouldUpdateSuccessfully() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("same@email.com");

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("New Name");
        dto.setEmail("same@email.com"); // Same email

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateUser(userId, dto);

        // Then
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("same@email.com");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).findByEmail(any()); // Should not check email since it's the same
        verify(userRepository).save(existingUser);
    }

    // ===============================
    // DELETE USER TESTS
    // ===============================

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(userId)
        );

        assertThat(exception.getMessage()).isEqualTo("User with ID 999 not found");
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private User createTestUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setPassword("password123");
        user.setEnabled(true);
        return user;
    }

    private UserUpdateRequestDTO createUpdateRequestDTO(String name, String email) {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}
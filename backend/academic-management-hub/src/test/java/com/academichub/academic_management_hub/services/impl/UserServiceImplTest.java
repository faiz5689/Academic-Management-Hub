package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.dto.UserDTO;
import com.academichub.academic_management_hub.exceptions.DuplicateEntityException;
import com.academichub.academic_management_hub.exceptions.EntityNotFoundException;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;
import com.academichub.academic_management_hub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .email("test@university.edu")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .passwordHash("hashed_password")
                .build();

        testUserDTO = UserDTO.builder()
                .id(userId)
                .email("test@university.edu")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.createUser(testUserDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUserDTO.getEmail());
        assertThat(result.getRole()).isEqualTo(testUserDTO.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(testUserDTO))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("User already exists with email");
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserByEmail(testUser.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    void getUserByEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@university.edu"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with email not found");
    }

    @Test
    void getAllUsers_Success() {
        User secondUser = new User();
        secondUser.setId(UUID.randomUUID());
        secondUser.setEmail("second@university.edu");
        secondUser.setRole(UserRole.STAFF);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, secondUser));

        var results = userService.getAllUsers();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getEmail()).isEqualTo(testUser.getEmail());
        assertThat(results.get(1).getEmail()).isEqualTo(secondUser.getEmail());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO updateDTO = testUserDTO.toBuilder()
                .email("updated@university.edu")
                .role(UserRole.STAFF)
                .build();

        UserDTO result = userService.updateUser(userId, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(any())).thenReturn(true);

        UserDTO updateDTO = testUserDTO.toBuilder()
                .email("existing@university.edu")
                .build();

        assertThatThrownBy(() -> userService.updateUser(userId, updateDTO))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("User already exists with email");
    }

    @Test
    void toggleUserActive_Success() {
        // Initial state
        testUser.setIsActive(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // Mock save to return user with toggled state
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            testUser.setIsActive(savedUser.getIsActive());
            return testUser;
        });

        UserDTO result = userService.toggleUserActive(userId);

        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isFalse(); // Should be toggled from true to false
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserActive_FromInactiveToActive_Success() {
        // Initial state
        testUser.setIsActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // Mock save to return user with toggled state
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            testUser.setIsActive(savedUser.getIsActive());
            return testUser;
        });

        UserDTO result = userService.toggleUserActive(userId);

        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isTrue(); // Should be toggled from false to true
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        userService.deleteUser(userId);
        
        verify(userRepository).delete(testUser);
    }
}
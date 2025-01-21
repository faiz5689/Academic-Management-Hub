package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.dto.UserDTO;
import com.academichub.academic_management_hub.exceptions.DuplicateEntityException;
import com.academichub.academic_management_hub.exceptions.EntityNotFoundException;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.UserRepository;
import com.academichub.academic_management_hub.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEntityException("User", "email", userDTO.getEmail());
        }

        User user = User.builder()
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .isActive(true)
                .passwordHash("temporary_hash") // This should be handled by auth service
                .createdAt(ZonedDateTime.now())
                .build();

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = findUserById(id);
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email", email));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = findUserById(id);

        // Check email uniqueness if email is being changed
        if (!user.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEntityException("User", "email", userDTO.getEmail());
        }

        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        if (userDTO.getIsActive() != null) {
            user.setIsActive(userDTO.getIsActive());
        }

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    @Override
    public UserDTO toggleUserActive(UUID id) {
        User user = findUserById(id);
        boolean newActiveStatus = !Boolean.TRUE.equals(user.getIsActive());
        user.setIsActive(newActiveStatus);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    // Helper methods
    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
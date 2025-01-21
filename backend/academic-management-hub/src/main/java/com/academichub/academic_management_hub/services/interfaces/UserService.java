// UserService.java
package com.academichub.academic_management_hub.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.academichub.academic_management_hub.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(UUID id, UserDTO userDTO);
    void deleteUser(UUID id);
    UserDTO toggleUserActive(UUID id);
}
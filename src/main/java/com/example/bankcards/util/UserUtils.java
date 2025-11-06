package com.example.bankcards.util;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;

/**
 * Utility class for user-related operations.
 * Provides methods for converting between User entities and DTOs.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
public class UserUtils {

    /**
     * Converts User entity to UserDTO.
     *
     * @param user user entity to convert
     * @return user DTO without sensitive information
     */
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
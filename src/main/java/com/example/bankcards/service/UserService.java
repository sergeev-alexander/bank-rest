package com.example.bankcards.service;

import com.example.bankcards.dto.ChangePasswordRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public interface UserService {

    User findById(Long id);

    User findByEmail(String email);

    Page<User> getAllUsers(@Nullable String email,
                           @Nullable Role role,
                           @Nullable LocalDateTime startDate,
                           @Nullable LocalDateTime endDate,
                           Pageable pageable);

    User createUser(CreateUserRequest request);

    void deleteUserById(Long id);

    void changePassword(Long userId, ChangePasswordRequest request);

    boolean existsByEmail(String email);

    boolean existsById(Long id);
}
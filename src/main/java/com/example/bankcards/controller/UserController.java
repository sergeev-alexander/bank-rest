package com.example.bankcards.controller;

import com.example.bankcards.dto.ChangePasswordRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.SecurityService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.PageableUtils;
import com.example.bankcards.util.UserUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SecurityService securityService;
    private final UserService userService;

    @Autowired
    public UserController(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        securityService.validateAdminAccess();

        return UserUtils.toDTO(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) {
        securityService.validateAdminAccess();

        return UserUtils.toDTO(userService.findByEmail(email));
    }

    @GetMapping
    public Page<UserDTO> getAllUsers(@RequestParam(required = false) String email,
                                     @RequestParam(required = false) Role role,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String[] sort) {

        securityService.validateAdminAccess();
        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        Page<User> users = userService.getAllUsers(email, role, startDate, endDate, pageable);

        return users.map(UserUtils::toDTO);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody @Valid CreateUserRequest request) {
        securityService.validateAdminAccess();
        User user = userService.createUser(request);

        return UserUtils.toDTO(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        securityService.validateAdminAccess();
        userService.deleteUserById(id);
    }

    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        Long currentUserId = securityService.getCurrentUserId();
        userService.changePassword(currentUserId, request);

        return ResponseEntity.ok().build();
    }
}
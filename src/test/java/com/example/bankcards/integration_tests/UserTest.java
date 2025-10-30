package com.example.bankcards.integration_tests;

import com.example.bankcards.TestDataFactory;
import com.example.bankcards.dto.ChangePasswordRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UserTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetUserByIdAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@bank.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void shouldDenyAccessToUserByIdAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetUserByEmailAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/users/email/admin@bank.com")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@bank.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void shouldGetAllUsersAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value("admin@bank.com"));
    }

    @Test
    void shouldCreateUserAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        CreateUserRequest request = new CreateUserRequest(
                "Jane", "Doe", "jane@test.com", "password123", Role.ROLE_USER);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane@test.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void shouldDenyCreateUserAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        CreateUserRequest request = new CreateUserRequest(
                "Jane", "Doe", "jane@test.com", "password123", Role.ROLE_USER);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteUserAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        CreateUserRequest request = new CreateUserRequest(
                "ToDelete", "User", "delete@test.com", "password123", Role.ROLE_USER);

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        long userId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/users/" + userId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldChangePasswordAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        ChangePasswordRequest request = new ChangePasswordRequest("password123", "newpassword123");

        mockMvc.perform(post("/api/users/me/password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest newLogin = new LoginRequest("john@test.com", "newpassword123");
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLogin)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestForInvalidPasswordChange() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        ChangePasswordRequest request = new ChangePasswordRequest("wrongpassword", "newpassword123");

        mockMvc.perform(post("/api/users/me/password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/users/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidUserCreation() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        CreateUserRequest invalidRequest = new CreateUserRequest(
                "", "", "invalid-email", "123", Role.ROLE_USER);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
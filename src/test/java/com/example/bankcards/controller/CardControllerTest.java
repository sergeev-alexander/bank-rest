package com.example.bankcards.controller;

import com.example.bankcards.BaseIntegrationTest;
import com.example.bankcards.TestDataFactory;
import com.example.bankcards.entity.CreateCardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CardControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCardAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        CreateCardRequest request = TestDataFactory.createCardRequest(userId);

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.maskedCardNumber").value("**** **** **** 3456"))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldDenyCreateCardAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        CreateCardRequest request = TestDataFactory.createCardRequest(1L);

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetCardByIdAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards/id/" + cardId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void shouldGetCardByIdAsOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);

        mockMvc.perform(get("/api/cards/id/" + cardId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId));
    }

    @Test
    void shouldDenyGetCardByIdAsNonOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards/id/" + cardId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetCardByNumberAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards/number/1234567890123456")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedCardNumber").value("**** **** **** 3456"));
    }

    @Test
    void shouldGetAllCardsAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(userId));
    }

    @Test
    void shouldDenyGetAllCardsAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/cards")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetUserCards() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);

        mockMvc.perform(get("/api/cards/my-cards")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(2));
    }

    @Test
    void shouldGetUserBalance() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);

        mockMvc.perform(get("/api/cards/my-balance")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.00"));
    }

    @Test
    void shouldGetUserBalanceByIdAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards/balance/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.00"));
    }

    @Test
    void shouldDenyGetUserBalanceByIdAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/cards/balance/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetUserCardsByUserIdAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(userId));
    }

    @Test
    void shouldBlockCardAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(post("/api/cards/" + cardId + "/admin-block")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void shouldDenyBlockCardAsUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);

        mockMvc.perform(post("/api/cards/" + cardId + "/admin-block")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldActivateCardAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(post("/api/cards/" + cardId + "/admin-block")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cards/" + cardId + "/activate")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldDeleteCardAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(delete("/api/cards/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cards/id/" + cardId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDenyDeleteCardAsUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);

        mockMvc.perform(delete("/api/cards/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundForNonExistentCard() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/cards/id/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidCardCreation() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        CreateCardRequest invalidRequest = TestDataFactory.createInvalidCardRequest();

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterCardsByStatus() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(post("/api/cards/" + cardId + "/admin-block")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cards?status=BLOCKED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("BLOCKED"));
    }

    @Test
    void shouldFilterCardsByUserId() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);

        mockMvc.perform(get("/api/cards?userId=" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(userId));
    }

    @Test
    void shouldHandlePaginationForUserCards() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        CreateCardRequest secondCard = TestDataFactory.createCardRequestWithBalance(2L, "9876543210987654", new BigDecimal("500.00"));
        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondCard)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cards/my-cards?page=0&size=1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
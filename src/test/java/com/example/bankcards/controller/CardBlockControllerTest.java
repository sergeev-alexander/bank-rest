package com.example.bankcards.controller;

import com.example.bankcards.BaseIntegrationTest;
import com.example.bankcards.TestDataFactory;
import com.example.bankcards.dto.CardBlockRequest;
import com.example.bankcards.entity.CreateCardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CardBlockControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBlockRequestAsCardOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        CardBlockRequest request = TestDataFactory.createCardBlockRequest(cardId);

        mockMvc.perform(post("/api/card-blocks")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldDenyCreateBlockRequestForNonOwnedCard() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        CardBlockRequest request = TestDataFactory.createCardBlockRequest(cardId);

        mockMvc.perform(post("/api/card-blocks")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetBlockRequestsByUserIdAsOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(get("/api/card-blocks/user/2")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldGetBlockRequestsByUserIdAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(get("/api/card-blocks/user/2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldDenyGetBlockRequestsByUserIdForOtherUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);

        mockMvc.perform(get("/api/card-blocks/user/" + userId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllBlockRequestsAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(get("/api/card-blocks")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldDenyGetAllBlockRequestsAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/card-blocks")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldApproveBlockRequestAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        Long blockId = TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(post("/api/card-blocks/" + blockId + "/approve")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldDenyApproveBlockRequestAsUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        Long blockId = TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(post("/api/card-blocks/" + blockId + "/approve")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestForInvalidBlockRequest() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        CardBlockRequest invalidRequest = TestDataFactory.createInvalidCardBlockRequest();

        mockMvc.perform(post("/api/card-blocks")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterBlockRequestsByStatus() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(get("/api/card-blocks?status=PENDING")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void shouldFilterBlockRequestsByCardId() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(get("/api/card-blocks?cardId=" + cardId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldFilterBlockRequestsByUserId() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId);

        mockMvc.perform(get("/api/card-blocks?userId=2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldHandlePaginationForBlockRequests() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId1 = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        
        CreateCardRequest secondCard = TestDataFactory.createCardRequestWithBalance(2L, "9876543210987654", new BigDecimal("500.00"));
        MvcResult result = mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondCard)))
                .andExpect(status().isOk())
                .andReturn();
        Long cardId2 = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId1);
        TestDataFactory.createTestCardBlock(mockMvc, objectMapper, userToken, cardId2);

        mockMvc.perform(get("/api/card-blocks/user/2?page=0&size=1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldReturnNotFoundForNonExistentBlockRequest() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(post("/api/card-blocks/999/approve")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateMultipleBlockRequestsForSameCard() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);

        CardBlockRequest request1 = TestDataFactory.createCardBlockRequest(cardId);
        CardBlockRequest request2 = TestDataFactory.createCardBlockRequest(cardId);

        mockMvc.perform(post("/api/card-blocks")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/card-blocks")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }
}
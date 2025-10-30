package com.example.bankcards.integration_tests;

import com.example.bankcards.TestDataFactory;
import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.entity.TransactionType;
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
class TransactionTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldDepositAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TransactionRequest request = TestDataFactory.createTransactionRequest(new BigDecimal("500.00"), TransactionType.DEPOSIT);

        mockMvc.perform(post("/api/transactions/deposit/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"));
    }

    @Test
    void shouldDenyDepositAsUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TransactionRequest request = TestDataFactory.createTransactionRequest(new BigDecimal("500.00"), TransactionType.DEPOSIT);

        mockMvc.perform(post("/api/transactions/deposit/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldWithdrawAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TransactionRequest request = TestDataFactory.createTransactionRequest(new BigDecimal("200.00"), TransactionType.WITHDRAW);

        mockMvc.perform(post("/api/transactions/withdraw/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId))
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.transactionType").value("WITHDRAW"));
    }

    @Test
    void shouldDenyWithdrawAsUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TransactionRequest request = TestDataFactory.createTransactionRequest(new BigDecimal("200.00"), TransactionType.WITHDRAW);

        mockMvc.perform(post("/api/transactions/withdraw/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetTransactionByIdAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        Long transactionId = TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("300.00"));

        mockMvc.perform(get("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.cardId").value(cardId));
    }

    @Test
    void shouldGetTransactionByIdAsOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        Long transactionId = TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("300.00"));

        mockMvc.perform(get("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId));
    }

    @Test
    void shouldDenyGetTransactionByIdAsNonOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        Long transactionId = TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("300.00"));

        mockMvc.perform(get("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetMyTransactions() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("500.00"));

        mockMvc.perform(get("/api/transactions/my-transactions")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldGetAllTransactionsAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("500.00"));

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldDenyGetAllTransactionsAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestForInvalidDeposit() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TransactionRequest invalidRequest = TestDataFactory.createInvalidTransactionRequest();

        mockMvc.perform(post("/api/transactions/deposit/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidWithdraw() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TransactionRequest invalidRequest = TestDataFactory.createInvalidTransactionRequest();

        mockMvc.perform(post("/api/transactions/withdraw/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterTransactionsByType() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("500.00"));

        mockMvc.perform(get("/api/transactions?transactionType=DEPOSIT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].transactionType").value("DEPOSIT"));
    }

    @Test
    void shouldFilterTransactionsByCardId() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("500.00"));

        mockMvc.perform(get("/api/transactions?cardId=" + cardId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldFilterTransactionsByUserId() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("500.00"));

        mockMvc.perform(get("/api/transactions?userId=" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].cardId").value(cardId));
    }

    @Test
    void shouldHandlePaginationForTransactions() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, 2L);
        
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("100.00"));
        TestDataFactory.createTestDeposit(mockMvc, objectMapper, adminToken, cardId, new BigDecimal("200.00"));

        mockMvc.perform(get("/api/transactions/my-transactions?page=0&size=1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldReturnNotFoundForNonExistentTransaction() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/transactions/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInsufficientFunds() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        Long cardId = TestDataFactory.createTestCard(mockMvc, objectMapper, adminToken, userId);
        TransactionRequest request = TestDataFactory.createTransactionRequest(new BigDecimal("2000.00"), TransactionType.WITHDRAW);

        mockMvc.perform(post("/api/transactions/withdraw/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}